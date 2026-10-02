#!/usr/bin/env python3
"""
from_ir_to_test.py - 根据 IR JSON 文件生成测试用例（正例 + 反例）

使用方法：
    直接运行脚本，输入 IR JSON 文件路径，脚本将为每条规则生成包含正反例的测试文件。
"""

import sys
import os
# 将父目录（Sepc2Checker）添加到 sys.path
sys.path.append(os.path.dirname(os.path.dirname(__file__)))

import json
from pathlib import Path
from typing import List

sys.path.append(os.path.dirname(__file__))  # Sepc2Checker 根目录

from ir_model import RuleIntentIR
from llm_client import OpenAIClient

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

def load_ir(filepath: str) -> List[RuleIntentIR]:
    """从 JSON 文件加载 IR 对象列表"""
    try:
        with open(filepath, 'r', encoding='utf-8') as f:
            data = json.load(f)
    except Exception as e:
        print(f"读取文件失败: {e}")
        sys.exit(1)

    if not isinstance(data, list):
        print("JSON 文件应该包含一个规则数组。")
        sys.exit(1)

    rules = []
    for idx, item in enumerate(data):
        try:
            rule = RuleIntentIR(**item)
            rules.append(rule)
        except Exception as e:
            print(f"第 {idx+1} 条规则解析失败: {e}")
    return rules

def generate_test_case_from_ir(ir: RuleIntentIR, client: OpenAIClient) -> str:
    """根据 IR 生成包含正反例的测试用例代码片段"""
    prompt = f"""You are a test case generator for static analysis rules. Given the following rule definition in IR format, generate a single file containing two code examples:

1. **Positive example** (should trigger the rule) – code that violates the rule.
2. **Negative example** (should NOT trigger the rule) – code that is compliant.

The examples must be realistic, concise, and clearly separated with comments. Use the target programming language: {ir.language}. 
Do not add extra explanations outside the code. Output only the code with comments.

### Rule IR
{ir.model_dump_json(indent=2)}

### Output Format Example
```{ir.language}
# Positive example (should trigger the rule)
<violating code>

# Negative example (should NOT trigger the rule)
<compliant code>
```"""
    try:
        response = client.client.chat.completions.create(
            model=client.model,
            messages=[{"role": "user", "content": prompt}],
            temperature=0.3,
            max_tokens=1500,
        )
        return response.choices[0].message.content.strip()
    except Exception as e:
        raise RuntimeError(f"Test case generation failed: {e}")

def main():
    if not API_KEY:
        raise RuntimeError("Set SPEC2CHECKER_API_KEY before running this script.")

    default_ir_file = "case_3_rules.json"
    filepath = input(f"请输入 IR JSON 文件路径（默认 {default_ir_file}）：").strip()
    if not filepath:
        filepath = default_ir_file

    print("正在加载 IR 规则...")
    rules = load_ir(filepath)
    if not rules:
        print("没有成功加载任何规则，程序退出。")
        sys.exit(1)
    print(f"成功加载 {len(rules)} 条规则。")

    client = OpenAIClient(api_key=API_KEY, base_url=BASE_URL, model=MODEL,
                          temperature=0.1, max_tokens=2000)

    OUTPUT_DIR.mkdir(parents=True, exist_ok=True)
    for ir in rules:
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
