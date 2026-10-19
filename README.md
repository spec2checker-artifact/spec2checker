# Spec2Checker

This repository contains a subset of the source code and experimental data for Spec2Checker.

## Included material

| Path | Contents |
| --- | --- |
| `Adapter/` | Adapters for CodeQL, PMD, and Semgrep |
| `LLM_to_cases/` | Test-case generation scripts |
| [`UIR/`](UIR/) | Unified intermediate representations of coding specifications |
| `XML_rules/` | Generated XML rules |
| `data/` | Evaluation data |
| `baseline/` | Baseline rule artifacts |
| [`ablation/`](ablation/) | Ablation experiment reports |
| `CodeQL_result/` | CodeQL generation outputs |
| `PMD_result/` | PMD generation outputs |
| `Semgrep_result/` | Semgrep generation outputs |
| [`validate_tool_result/`](validate_tool_result/) | Tool-validation outputs and preliminary `Rule_sem` candidates |
| `cost_analysis/` | Cost-analysis data and figures |
| [`data_summary.md`](data_summary.md) | Summary of experimental results |

## Evaluation note

The preliminary `Rule_sem` candidates in `validate_tool_result/` underwent additional manual screening for semantic correctness. Final `Rule_sem` counts and evaluation metrics are based on the corresponding experimental run data.
