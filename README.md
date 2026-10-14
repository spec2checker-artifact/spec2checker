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
| `validate_tool_result/` | Tool-validation reports and preliminary `Rule_sem` candidate lists; see the evaluation note below |
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

## Interpretation of `validate_tool_result/`

The [`validate_tool_result/`](validate_tool_result/) directory contains
tool-validation reports and validated rule lists used as preliminary candidates
for `Rule_sem` (rules that correctly express the semantics of their coding
specifications). These files capture an intermediate stage of the evaluation.

To improve rule accuracy, we subsequently performed additional manual screening
to check whether the candidate rules correctly represented the intended
specification semantics. The final rule sets and `Rule_sem` counts therefore
reflect this further screening.

**The actual experimental run data are authoritative for the final rule counts
and evaluation metrics.** In particular, the syntax-validation success counts
and `validated_count` values in this directory do not by themselves establish
semantic correctness and should not be treated as the final `Rule_sem` counts
or final rule-usability results. Consult the corresponding experimental run records for the setting, batch,
model, and method being evaluated.

[`data_summary.md`](data_summary.md) provides a summary of the reported
experimental values. If a summary differs from the underlying run records,
check the corresponding experimental batch and version against the actual run
data.

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
