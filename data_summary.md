# Spec2Checker 论文数据汇总

本文档汇总用户提供的论文版本中的数值，用于核对论文各节、表格和结论。这里的论文值不等同于重新核验后的项目实验结果；项目原始文件的统计结果见 `result/quality2_statistics.md`。两者不一致时，应回查对应批次和版本的原始记录。

## 实验规模与设置

| 项目 | 数据 |
|---|---:|
| 编程规范总数 | 600 |
| 编程语言 | C/C++、Java、JavaScript、Python、Shell |
| 每种语言规范数 | 120 |
| Easy / Hard 规范数 | 各 300 |
| Evaluation sets 数量 | 6 |
| 每个 evaluation set 规范数 | 100，包含 50 easy 和 50 hard |
| 每个 set、每种语言抽取数 | 10 easy 和 10 hard |
| 开源项目 | Bazel、Letta、OpenPLC-v3 |
| 静态分析工具 | PMD 6.55.0、Semgrep 1.156.0、CodeQL 2.25.0 |
| LLM | Qwen3-Coder、DeepSeek-V3.2、DeepSeek-V4、GLM-5.1 |
| RQ1 / RQ2 固定模型 | DeepSeek-V4 |
| RQ3 / RQ4 模型范围 | 四个模型 |
| 最大 refinement 次数 | 3 |
| Temperature | 0.2 |

## 指标与聚合口径

- `R_comp`：能够由目标工具解析、编译并执行的规则数量。
- `R_sem`：能够正确表达规范语义的可编译规则数量。
- `R_use = R_sem / 50 × 100%`。
- `Det.`：报告的违规数量。
- `Cor.`：人工确认的真实违规数量。
- `Prec. = Cor. / Det. × 100%`。
- Easy 和 hard 分别对六个 evaluation sets 的计数求平均，再计算可用率和 precision。
- `Avg. R_use` 和 `Avg. Prec.` 是对应 easy、hard 比率的算术平均。
- 计算应使用未舍入的原始均值，最后统一保留两位小数；不能先舍入计数再计算比率。

## RQ1：规则生成质量

以下是论文 Table quality1 在固定 DeepSeek-V4 下的报告值。

| Method | Easy Rcomp | Hard Rcomp | Easy Rsem | Hard Rsem | Easy Ruse | Hard Ruse | Avg. Ruse |
|---|---:|---:|---:|---:|---:|---:|---:|
| DirectLLM | 16.17 | 9.17 | 10.17 | 5.17 | 20.34% | 10.34% | 15.34% |
| TemplateLLM | 28.17 | 16.17 | 23.17 | 11.17 | 46.34% | 22.34% | 34.34% |
| CoT-LLM | 30.17 | 18.17 | 24.17 | 13.17 | 48.34% | 26.34% | 37.34% |
| Spec2Checker | 46.00 | 48.33 | 46.00 | 45.67 | 92.00% | 91.34% | 91.67% |

论文报告完整方法的平均规则可用率为 91.67%，相较 CoT-LLM 的 37.34% 高 54.33 个百分点。

## RQ2：检测能力

以下是论文 Table quality1 的检测报告值，固定模型为 DeepSeek-V4。

| Method | Easy Det. | Easy Cor. | Easy Prec. | Hard Det. | Hard Cor. | Hard Prec. | Avg. Prec. |
|---|---:|---:|---:|---:|---:|---:|---:|
| DirectLLM | 81.17 | 17.83 | 21.97% | 31.83 | 5.17 | 16.24% | 19.11% |
| TemplateLLM | 252.50 | 88.17 | 34.92% | 90.17 | 22.33 | 24.76% | 29.84% |
| CoT-LLM | 263.33 | 100.33 | 38.10% | 106.17 | 28.83 | 27.15% | 32.63% |
| Spec2Checker | 392.25 | 345.33 | 88.04% | 305.29 | 286.33 | 93.79% | 90.92% |

按论文展示值计算，完整方法 easy 误报数为 46.92，hard 误报数为 18.96；CoT-LLM 分别为 163.00 和 77.34。

补充规范实验报告：Spec2Checker 对 Python 3.12 `typing.override` 和 Java 25 `MemorySegment` 的对应示例均全部通过；DirectLLM 分别通过 40% 和 60% 的示例。

## RQ3：成本

| 指标 | Easy | Hard |
|---|---|---|
| 50 条规范的总生成时间 | 342–841 秒 | 502–1,820 秒 |
| Token 消耗 | 1,255K–1,968K | 730K–2,656K |
| 单条规则平均时间 | 小于 60 秒 | 小于 60 秒 |
| 每个 50 规范集合成本 | 小于 0.30 USD | 小于 0.30 USD |
| Refinement 时间占比 | 36%–76%（跨模型和难度总体范围） | 36%–76%（跨模型和难度总体范围） |

## RQ4：消融方法定义

- `Spec2Checker-WoUIR`：移除 UIR，保留 validation-driven refinement。
- `Spec2Checker-WoIter`：保留 UIR，只执行单次生成，不进行 validation-driven refinement。
- `Spec2Checker`：完整方法。
- `CoT-LLM`：独立基线，不能与 WoUIR 等同。

论文 caption 声明 WoIter 每次、每个难度随机抽取 100 个报告结果进行审计。因此其 `Det. = 100.00` 是审计样本量，不是全量检测数；其 `Cor.` 也只是样本内正确数量。

## RQ4：规则质量报告值

| Method | Model | Rcomp e | Rcomp h | Rsem e | Rsem h | Ruse e | Ruse h | Avg. Ruse |
|---|---|---:|---:|---:|---:|---:|---:|---:|
| WoUIR | Qwen3-Coder | 19.17 | 12.17 | 16.17 | 8.17 | 32.34% | 16.34% | 24.34% |
| WoUIR | DeepSeek-V3.2 | 24.17 | 14.17 | 20.17 | 10.17 | 40.34% | 20.34% | 30.34% |
| WoUIR | DeepSeek-V4 | 30.17 | 18.17 | 24.17 | 13.17 | 48.34% | 26.34% | 37.34% |
| WoUIR | GLM-5.1 | 28.17 | 16.17 | 22.17 | 11.17 | 44.34% | 22.34% | 33.34% |
| WoIter | Qwen3-Coder | 46.00 | 47.33 | 44.17 | 43.17 | 88.34% | 86.34% | 87.34% |
| WoIter | DeepSeek-V3.2 | 48.33 | 47.50 | 45.17 | 44.17 | 90.34% | 88.34% | 89.34% |
| WoIter | DeepSeek-V4 | 46.00 | 48.33 | 45.17 | 44.17 | 90.34% | 88.34% | 89.34% |
| WoIter | GLM-5.1 | 46.00 | 48.33 | 46.00 | 44.50 | 92.00% | 89.00% | 90.50% |
| Spec2Checker | Qwen3-Coder | 46.00 | 47.33 | 44.17 | 46.33 | 88.34% | 92.66% | 90.50% |
| Spec2Checker | DeepSeek-V3.2 | 48.33 | 47.50 | 46.17 | 45.33 | 92.34% | 90.66% | 91.50% |
| Spec2Checker | DeepSeek-V4 | 46.00 | 48.33 | 46.00 | 45.67 | 92.00% | 91.34% | 91.67% |
| Spec2Checker | GLM-5.1 | 46.00 | 48.33 | 46.33 | 47.00 | 92.66% | 94.00% | 93.33% |

## RQ4：检测性能报告值

| Method | Model | Easy Det. | Easy Cor. | Easy Prec. | Hard Det. | Hard Cor. | Hard Prec. | Avg. Prec. |
|---|---|---:|---:|---:|---:|---:|---:|---:|
| WoUIR | Qwen3-Coder | 128.17 | 39.33 | 30.69% | 41.67 | 8.17 | 19.61% | 25.15% |
| WoUIR | DeepSeek-V3.2 | 179.67 | 60.17 | 33.49% | 61.83 | 14.00 | 22.64% | 28.07% |
| WoUIR | DeepSeek-V4 | 263.33 | 100.33 | 38.10% | 106.17 | 28.83 | 27.15% | 32.63% |
| WoUIR | GLM-5.1 | 219.50 | 79.17 | 36.07% | 79.00 | 19.67 | 24.90% | 30.49% |
| WoIter | Qwen3-Coder | 100.00 | 26.00 | 26.00% | 100.00 | 4.00 | 4.00% | 15.00% |
| WoIter | DeepSeek-V3.2 | 100.00 | 26.00 | 26.00% | 100.00 | 5.00 | 5.00% | 15.50% |
| WoIter | DeepSeek-V4 | 100.00 | 33.00 | 33.00% | 100.00 | 9.00 | 9.00% | 21.00% |
| WoIter | GLM-5.1 | 100.00 | 32.00 | 32.00% | 100.00 | 9.00 | 9.00% | 20.50% |
| Spec2Checker | Qwen3-Coder | 166.17 | 157.33 | 94.68% | 351.00 | 330.17 | 94.07% | 94.38% |
| Spec2Checker | DeepSeek-V3.2 | 182.00 | 168.17 | 92.40% | 402.83 | 358.67 | 89.04% | 90.72% |
| Spec2Checker | DeepSeek-V4 | 392.25 | 345.33 | 88.04% | 305.29 | 286.33 | 93.79% | 90.92% |
| Spec2Checker | GLM-5.1 | 360.83 | 297.67 | 82.50% | 301.00 | 269.83 | 89.64% | 86.07% |

## 完整方法的模型级汇总

| LLM | Combined Det. | Combined Cor. | Avg. Prec. |
|---|---:|---:|---:|
| Qwen3-Coder | 517.17 | 487.50 | 94.38% |
| DeepSeek-V3.2 | 584.83 | 526.84 | 90.72% |
| DeepSeek-V4 | 697.54 | 631.66 | 90.92% |
| GLM-5.1 | 661.83 | 567.50 | 86.07% |

Combined Det. / Cor. 是 easy、hard 平均计数之和。Avg. Prec. 是两个难度 precision 的算术平均，不是 Combined Cor. / Combined Det.。

## 论文中的主要数据结论

| 项目 | 论文报告范围或最高值 |
|---|---|
| 完整方法 Avg. Ruse | 90.50%–93.33% |
| WoUIR Avg. Ruse | 24.34%–37.34% |
| WoIter Avg. Ruse | 87.34%–90.50% |
| 完整方法 Avg. Prec. | 86.07%–94.38% |
| WoUIR Avg. Prec. | 25.15%–32.63% |
| WoIter 样本 Avg. Prec. | 15.00%–21.00% |
| 完整方法 Combined Det. | 517.17–697.54 |
| 完整方法 Combined Cor. | 487.50–631.66 |
| WoUIR Combined Cor. | 47.50–129.16 |
| WoIter 样本 Combined Cor. | 30.00–42.00 |
| 最高平均 precision | Qwen3-Coder，94.38% |
| 最高平均规则可用率 | GLM-5.1，93.33% |
| 最高 Combined Cor. | DeepSeek-V4，631.66 |

## 实际项目反馈

| 指标 | 论文报告值 |
|---|---:|
| 向维护者报告的实际违规 | 24 |
| 合并 Pull Requests | 7 |
| 确认 Issues | 3 |

摘要的 631.66 对应 DeepSeek-V4 的 Combined Cor.。描述该值时应注明模型设置，避免理解为四模型总体平均。

## 需要回查的统计一致性问题

- 六次整数计数的均值只能是整数总数除以 6。392.25 和 305.29 无法直接由六次整数 Det. 的算术平均产生，需要说明是否存在额外的平均层级或分母。
- 部分 Ruse 使用已舍入的 Rsem 再计算，例如 10.17 / 50 = 20.34%。如果原始总数为 61，严格按未舍入均值计算应为 61 / 300 × 100% = 20.33%。所有比率应从原始计数计算后再舍入。
- 完整 GLM-5.1 easy 行 Rsem = 46.33 大于 Rcomp = 46.00，与论文中“语义正确规则是可编译规则的子集”的定义冲突。
- 论文 WoUIR 的 DeepSeek-V4 行与 CoT-LLM 行完全一致，但两个方法定义不同；是否相等应由独立实验记录确认。
- 已查看的部分项目审计报告是按置信度选取的样本；论文 caption 的“随机抽样”描述需要对应的抽样记录支持。
- WoIter 样本 Cor. 与完整方法全量 Cor. 不具有相同分母，不能直接用其数量差异证明 refinement 增加了正确检测数。
- Table quality2 的 caption 声明最佳值加粗，但用户提供版本没有加粗数据。
- 论文中的最高 precision 94.38% 指平均 precision；单独 easy precision 的最高值是 94.68%，应在摘要等处明确平均口径。
- 根目录外的全量报告不能仅因位于 iteration 文件夹外就判定为最终迭代。统计首次运行与最终迭代时应根据生成过程和实际迭代编号确认。

以上问题不改变本文档对论文数值的记录，但在将这些数值用于最终论文结论前，需要根据原始实验记录统一口径。
