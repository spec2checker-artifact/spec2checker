# Spec2Checker Anonymous Artifact Preview

This repository contains an anonymized, review-stage subset of the source code,
data, generated rules, and evaluation outputs for Spec2Checker. Author and
institution information is intentionally omitted for double-blind review.

## Included material

| Path | Contents |
| --- | --- |
| `Adapter/` | Adapters for CodeQL, PMD, and Semgrep rule generation |
| `LLM_to_cases/` | LLM-based test-case generation scripts |
| `XML_rules/` | Generated XML rule artifacts |
| `data/` | The currently released evaluation data subset |
| `baseline/` | Baseline rule artifacts |
| `CodeQL_result/` | CodeQL generation outputs |
| `PMD_result/` | PMD generation outputs |
| `Semgrep_result/` | Semgrep generation outputs |
| `validate_tool_result/` | Syntax-validation outputs |
| `cost_analysis/` | Cost-analysis data and figures |
| `data_summary.md` | Summary of the released data and results |

This is a partial preview rather than the complete archival artifact. Additional
pipeline components, datasets, and experiment outputs are being organized and
will be included in the final public release upon acceptance.

## Notes

- Generated rule files are preserved as raw experimental outputs. Some files are
  intentionally syntactically invalid because rule-generation validity is one of
  the evaluated outcomes.
- Credentials are not included. Scripts that call an LLM read configuration from
  environment variables; see `.env.example`.
- Some scripts depend on pipeline components that are not part of this partial
  preview and therefore are not yet runnable as a complete end-to-end system.

## Python dependencies

The Python packages used by the released scripts are listed in
`requirements.txt`. Install them with:

```bash
python -m pip install -r requirements.txt
```

## Data availability

An anonymized snapshot of our source code and data is available in this
repository. The final archival version will be made publicly available upon
acceptance.
