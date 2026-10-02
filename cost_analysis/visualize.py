import os
import re
import pandas as pd
import numpy as np
import matplotlib.pyplot as plt
from pathlib import Path

BASE_DIR = Path(__file__).parent
CHARTS_DIR = BASE_DIR / "charts"
CHARTS_DIR.mkdir(exist_ok=True)

MODELS = ["DeepSeek-V3-2", "DeepSeek-V4", "GLM-5-1", "Qwen3-Coder-Plus"]
MODEL_LABELS = ["DeepSeek-V3-2", "DeepSeek-V4", "GLM-5-1", "Qwen3-Coder"]
BATCHES_EASY = ["easy_1st", "easy_2nd", "easy_3rd", "easy_4th", "easy_5th", "easy_6th"]
BATCHES_HARD = ["hard_1st", "hard_2nd", "hard_3rd", "hard_4th", "hard_5th", "hard_6th"]
BATCHES_ALL = BATCHES_EASY + BATCHES_HARD

COLOR_EASY = "#A8D5BA"
COLOR_HARD = "#F4A261"
INPUT_PRICE = {"DeepSeek-V3-2": 2, "DeepSeek-V4": 12, "GLM-5-1": 6, "Qwen3-Coder-Plus": 4}
OUTPUT_PRICE = {"DeepSeek-V3-2": 3, "DeepSeek-V4": 24, "GLM-5-1": 24, "Qwen3-Coder-Plus": 16}
RMB_TO_USD = 7.2
COMPUTE_RATE_USD_PER_HOUR = 0.50

# 全局字体设置：图片中文字少而大
plt.rcParams["font.family"] = "Microsoft YaHei"
plt.rcParams["axes.unicode_minus"] = False
plt.rcParams["figure.dpi"] = 150
plt.rcParams["font.size"] = 24          # 全局基础字号


def parse_cost_analysis(filepath):
    data = {}
    text = filepath.read_text(encoding="utf-8")
    m = re.search(r"批次:\s*(.+)", text)
    data["batch"] = m.group(1).strip() if m else ""
    m = re.search(r"模型:\s*(.+)", text)
    data["model"] = m.group(1).strip() if m else ""
    m = re.search(r"总耗时:\s*([\d.]+)s", text)
    data["total_time"] = float(m.group(1)) if m else 0
    m = re.search(r"Step 1:.*?:\s*([\d.]+)s", text)
    data["step1_time"] = float(m.group(1)) if m else 0
    m = re.search(r"Step 2:.*?:\s*([\d.]+)s", text)
    data["step2_time"] = float(m.group(1)) if m else 0
    m = re.search(r"Step 1:.*?stdout=([\d]+)\s*words", text)
    data["step1_words"] = int(m.group(1)) if m else 0
    m = re.search(r"Step 2:.*?stdout=([\d]+)\s*words", text)
    data["step2_words"] = int(m.group(1)) if m else 0
    data["total_words"] = data["step1_words"] + data["step2_words"]
    return data


def parse_cost_iteration(filepath):
    data = {}
    text = filepath.read_text(encoding="utf-8")
    m = re.search(r"批次:\s*(.+)", text)
    data["batch"] = m.group(1).strip() if m else ""
    m = re.search(r"模型:\s*(.+)", text)
    data["model"] = m.group(1).strip() if m else ""
    m = re.search(r"迭代:\s*(\d+)", text)
    data["iteration"] = int(m.group(1)) if m else 0
    m = re.search(r"总耗时:\s*([\d.]+)s", text)
    data["total_time"] = float(m.group(1)) if m else 0
    total_words = 0
    for m in re.finditer(r"~?([\d]+)\s*words\s*/\s*~?([\d]+)\s*chars", text):
        if "合计" in text[m.start():] or "scan stdout" in text[m.start():]:
            total_words += int(m.group(1))
    data["words"] = total_words
    return data


def load_cumulative_data():
    rows = []
    for batch in BATCHES_ALL:
        batch_dir = BASE_DIR / batch
        if not batch_dir.exists():
            continue
        for model_dir in batch_dir.iterdir():
            if not model_dir.is_dir():
                continue
            model = model_dir.name
            ca_file = model_dir / "cost_analysis.txt"
            init_time = 0
            init_step1_time = 0
            init_step1_words = 0
            if ca_file.exists():
                d = parse_cost_analysis(ca_file)
                init_time = d["total_time"]
                init_step1_time = d["step1_time"]
                init_step1_words = d["step1_words"]
            iter_time = 0
            iter_words = 0
            for f in model_dir.iterdir():
                if f.name.startswith("cost_iteration") and f.name.endswith(".txt"):
                    d = parse_cost_iteration(f)
                    iter_time += d["total_time"]
                    iter_words += d["words"]
            cumul_time = init_time + iter_time
            llm_words = init_step1_words + iter_words
            compute_cost = cumul_time / 3600 * COMPUTE_RATE_USD_PER_HOUR
            input_tokens = llm_words * 10
            api_cost_rmb = (input_tokens / 1e6 * INPUT_PRICE[model]
                            + llm_words / 1e6 * OUTPUT_PRICE[model])
            api_cost_usd = api_cost_rmb / RMB_TO_USD
            rows.append({
                "batch": batch,
                "model": model,
                "difficulty": "easy" if batch.startswith("easy") else "hard",
                "cumul_time": cumul_time,
                "init_step1_time": init_step1_time,
                "init_step2_time": init_time - init_step1_time,
                "iter_time": iter_time,
                "llm_words": llm_words,
                "api_cost_usd": api_cost_usd,
                "compute_cost_usd": compute_cost,
                "total_cost_usd": api_cost_usd + compute_cost,
            })
    return pd.DataFrame(rows)


def add_labels(ax, bars, fmt="{:.0f}", fontsize=22, offset=5):
    """给柱状图添加数值标签"""
    for b in bars:
        h = b.get_height()
        if h > 0:
            ax.text(b.get_x() + b.get_width() / 2, h + offset,
                    fmt.format(h), ha="center", va="bottom", fontsize=fontsize)


def draw_grouped_bars(ax, easy_vals, hard_vals, label_fmt, ylabel, tag):
    """绘制分组柱状图，无图例，仅保留极简标签和数值"""
    x = np.arange(len(MODELS))
    w = 0.35
    be = ax.bar(x - w / 2, easy_vals, w, color=COLOR_EASY, edgecolor="white")
    bh = ax.bar(x + w / 2, hard_vals, w, color=COLOR_HARD, edgecolor="white")

    add_labels(ax, be, fmt=label_fmt, fontsize=22, offset=5)
    add_labels(ax, bh, fmt=label_fmt, fontsize=22, offset=5)

    ax.set_xticks(x)
    ax.set_xticklabels(MODEL_LABELS, fontsize=22, rotation=15, ha="right")
    ax.set_ylabel(ylabel, fontsize=26)
    ax.set_title(tag, fontsize=28, fontweight="bold", loc="left", pad=10)

    ax.grid(axis="y", alpha=0.3)
    ymax = max(max(hard_vals), max(easy_vals))
    ax.set_ylim(0, ymax * 1.3)   # 留足标签空间


def combined_chart(df):
    # 使用较大的画布，手动调整子图间距，避免 constrained_layout 报错
    fig, axes = plt.subplots(2, 2, figsize=(24, 16))
    plt.subplots_adjust(left=0.08, right=0.95, top=0.95, bottom=0.08,
                        wspace=0.25, hspace=0.35)

    # (a) 时间成本
    easy_time = df[df["difficulty"] == "easy"].groupby("model")["cumul_time"].mean()
    hard_time = df[df["difficulty"] == "hard"].groupby("model")["cumul_time"].mean()
    draw_grouped_bars(axes[0, 0],
                      [easy_time.get(m, 0) for m in MODELS],
                      [hard_time.get(m, 0) for m in MODELS],
                      "{:.0f}s", "Time (s)", "(a)")

    # (b) Token 消耗
    easy_tok = df[df["difficulty"] == "easy"].groupby("model")["llm_words"].mean()
    hard_tok = df[df["difficulty"] == "hard"].groupby("model")["llm_words"].mean()
    draw_grouped_bars(axes[0, 1],
                      [easy_tok.get(m, 0) for m in MODELS],
                      [hard_tok.get(m, 0) for m in MODELS],
                      "{:.0f}", "Tokens", "(b)")

    # (c) 经济成本
    easy_cost = df[df["difficulty"] == "easy"].groupby("model")["total_cost_usd"].mean()
    hard_cost = df[df["difficulty"] == "hard"].groupby("model")["total_cost_usd"].mean()
    draw_grouped_bars(axes[1, 0],
                      [easy_cost.get(m, 0) for m in MODELS],
                      [hard_cost.get(m, 0) for m in MODELS],
                      "USD {:.2f}", "Cost (USD)", "(c)")

    # (d) 堆叠时间分解
    easy_init = df[df["difficulty"] == "easy"].groupby("model")["cumul_time"].mean()
    easy_iter = df[df["difficulty"] == "easy"].groupby("model")["iter_time"].mean()
    hard_init = df[df["difficulty"] == "hard"].groupby("model")["cumul_time"].mean()
    hard_iter = df[df["difficulty"] == "hard"].groupby("model")["iter_time"].mean()
    init_only_easy = easy_init - easy_iter
    init_only_hard = hard_init - hard_iter

    ax4 = axes[1, 1]
    x = np.arange(len(MODELS))
    w = 0.35
    ax4.bar(x - w / 2, [init_only_easy.get(m, 0) for m in MODELS], w,
            color=COLOR_EASY, edgecolor="white")
    ax4.bar(x - w / 2, [easy_iter.get(m, 0) for m in MODELS], w,
            bottom=[init_only_easy.get(m, 0) for m in MODELS],
            color="#C8E6C9", edgecolor="white", hatch="//")
    ax4.bar(x + w / 2, [init_only_hard.get(m, 0) for m in MODELS], w,
            color=COLOR_HARD, edgecolor="white")
    ax4.bar(x + w / 2, [hard_iter.get(m, 0) for m in MODELS], w,
            bottom=[init_only_hard.get(m, 0) for m in MODELS],
            color="#FFD9B1", edgecolor="white", hatch="//")

    # 仅标注总量
    for i, m in enumerate(MODELS):
        total_e = easy_init.get(m, 0)
        total_h = hard_init.get(m, 0)
        ax4.text(x[i] - w / 2, total_e + 5, f"{total_e:.0f}s",
                 ha="center", va="bottom", fontsize=22)
        ax4.text(x[i] + w / 2, total_h + 5, f"{total_h:.0f}s",
                 ha="center", va="bottom", fontsize=22)

    ax4.set_xticks(x)
    ax4.set_xticklabels(MODEL_LABELS, fontsize=22, rotation=15, ha="right")
    ax4.set_ylabel("Time (s)", fontsize=26)
    ax4.set_title("(d)", fontsize=28, fontweight="bold", loc="left", pad=10)
    ax4.grid(axis="y", alpha=0.3)
    all_totals = [easy_init.get(m, 0) for m in MODELS] + [hard_init.get(m, 0) for m in MODELS]
    ax4.set_ylim(0, max(all_totals) * 1.3)

    # 保存图片（不使用 bbox_inches="tight"，避免与手动布局冲突）
    fig.savefig(CHARTS_DIR / "combined_chart.png", dpi=300)
    fig.savefig(CHARTS_DIR / "image6.pdf", format="pdf", dpi=300)
    plt.close(fig)


def main():
    print("Loading data...")
    df = load_cumulative_data()
    print(f"  records: {len(df)}")
    print("\nGenerating combined chart...")
    combined_chart(df)
    print("  Combined chart done")
    print(f"\nSaved to: {CHARTS_DIR}")
    for f in sorted(CHARTS_DIR.glob("combined*.png")):
        print(f"  {f.name} ({f.stat().st_size / 1024:.1f} KB)")


if __name__ == "__main__":
    main()
