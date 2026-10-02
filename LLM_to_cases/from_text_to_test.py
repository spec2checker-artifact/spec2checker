#!/usr/bin/env python3
"""
from_text_to_test.py - 从自然语言描述生成精炼测试用例（正例 + 反例）

使用方法：
    直接运行脚本，输入要生成的测试用例数量，然后逐个输入自然语言描述（空行结束），
    脚本将调用 LLM 生成 IR，再基于 IR 生成包含正反例的测试文件，保存到指定目录。
"""

import sys
import os
# 将父目录（Sepc2Checker）添加到 sys.path
sys.path.append(os.path.dirname(os.path.dirname(__file__)))

from pathlib import Path
from typing import List

# 将项目根目录和 LLM 子目录加入路径
sys.path.append(os.path.dirname(os.path.dirname(__file__)))  # Sepc2Checker 根目录

from LLM_to_IR.ir_model import RuleIntentIR
from LLM_to_IR.llm_client import OpenAIClient

# ========== 配置 ==========
API_KEY = os.environ.get("SPEC2CHECKER_API_KEY", "")
BASE_URL = os.environ.get("SPEC2CHECKER_BASE_URL") or None
MODEL = os.environ.get("SPEC2CHECKER_MODEL", "gpt-4o")
OUTPUT_DIR = Path(
    os.environ.get(
        "SPEC2CHECKER_TEST_OUTPUT_DIR",
        str(Path(__file__).resolve().parents[1] / "test_cases" / "generated"),
    )
)
# ===========================

def generate_test_case_from_ir(ir: RuleIntentIR, client: OpenAIClient) -> str:
    """根据 IR 生成包含正反例的精炼测试用例代码片段"""
    # 针对 Java 的特殊说明
    java_note = ""
    if ir.language == "java":
        java_note = "- For Java, use different class names for positive and negative examples (e.g., PositiveExample and NegativeExample) to avoid compilation errors.\n"

    prompt = f"""You are a test case generator for static analysis rules. Given the following rule definition in IR format, generate a single file containing two concise code examples:

1. **Positive example** (should trigger the rule) – minimal code that violates the rule.
2. **Negative example** (should NOT trigger the rule) – minimal code that is compliant.

Requirements:
- Keep the examples short and focused on the rule's core logic.
- Use the target programming language: {ir.language}.
- Do not add extra comments or explanation outside the code.
- Output ONLY the code, with a single blank line separating the two examples.
- Do NOT wrap the code in markdown code fences (```).
{java_note}
### Rule IR
{ir.model_dump_json(indent=2)}

### Output Format Example
# Positive example (should trigger the rule)
<violating code>

# Negative example (should NOT trigger the rule)
<compliant code>"""
    try:
        response = client.client.chat.completions.create(
            model=client.model,
            messages=[{"role": "user", "content": prompt}],
            temperature=0.3,
            max_tokens=1000,   # 减少 token 以鼓励精简
        )
        return response.choices[0].message.content.strip()
    except Exception as e:
        raise RuntimeError(f"Test case generation failed: {e}")

def main():
    if not API_KEY:
        raise RuntimeError("Set SPEC2CHECKER_API_KEY before running this script.")

    # 获取规则数量
    while True:
        try:
            num = int(input("请输入要生成的测试用例数量: ").strip())
            if num > 0:
                break
            print("数量必须大于0，请重新输入。")
        except ValueError:
            print("请输入有效的整数。")

    client = OpenAIClient(api_key=API_KEY, base_url=BASE_URL, model=MODEL,
                          temperature=0.1, max_tokens=2000)

    rules = []
    failed = []

    # 1. 生成 IR
    for i in range(1, num + 1):
        print(f"\n--- 输入第 {i} 条描述（输入空行结束） ---")
        lines = []
        while True:
            try:
                line = sys.stdin.readline()
            except KeyboardInterrupt:
                print("\n输入已取消。")
                sys.exit(1)
            if not line:
                break
            line = line.rstrip('\n')
            if line == "":
                break
            lines.append(line)

        spec = "\n".join(lines).strip()
        if not spec:
            print(f"警告：第 {i} 条描述为空，跳过。")
            failed.append(i)
            continue

        print(f"正在调用 LLM 生成第 {i} 条测试用例")
        try:
            ir = client.generate_ir(spec)
            rules.append(ir)
            print(f"第 {i} 条测试用例生成成功")
        except Exception as e:
            print(f"第 {i} 条测试用例生成失败: {e}")
            failed.append(i)

    if not rules:
        print("没有成功生成任何规则，程序退出。")
        sys.exit(1)

    if failed:
        print(f"注意：以下测试用例生成失败：{failed}")

    # 2. 生成测试用例
    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    for ir in rules:
        # 根据语言确定文件扩展名
        ext_map = {"python": "py", "java": "java", "shell": "sh"}
        ext = ext_map.get(ir.language, "txt")
        filename = f"{ir.rule_id}_testcase.{ext}"
        filepath = OUTPUT_DIR / filename

        print(f"正在为规则 {ir.rule_id} 生成测试用例...")
        try:
            test_code = generate_test_case_from_ir(ir, client)
            with open(filepath, 'w', encoding='utf-8') as f:
                f.write(test_code)
            print(f"生成测试文件: {filepath}")
        except Exception as e:
            print(f"生成 {ir.rule_id} 测试用例失败: {e}")

    print(f"\n所有测试用例已生成到: {OUTPUT_DIR}")

if __name__ == "__main__":
    main()
