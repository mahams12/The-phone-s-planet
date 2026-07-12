import 'package:flutter/material.dart';

import '../data/budget.dart';
import '../data/phone.dart';
import '../theme/colors.dart';
import '../util/formatters.dart';
import '../widgets/common.dart';

class BudgetScreen extends StatefulWidget {
  const BudgetScreen({super.key, required this.phones});

  final List<Phone> phones;

  @override
  State<BudgetScreen> createState() => _BudgetScreenState();
}

class _BudgetScreenState extends State<BudgetScreen> {
  String? _selectedMonth; // null = All time

  @override
  Widget build(BuildContext context) {
    final months = availableMonths(widget.phones);

    // Keep selection valid if the underlying data changed.
    if (_selectedMonth != null && !months.contains(_selectedMonth)) {
      _selectedMonth = null;
    }

    final summary = budgetForMonth(widget.phones, _selectedMonth);
    final breakdown = monthlyBreakdown(widget.phones);
    final wide = MediaQuery.sizeOf(context).width > 700;

    return ListView(
      padding: const EdgeInsets.only(bottom: 32),
      children: [
        const Text(
          'Budget',
          style: TextStyle(
            fontSize: 24,
            fontWeight: FontWeight.w800,
            color: TppColors.textPrimary,
          ),
        ),
        const SizedBox(height: 4),
        Text(
          _selectedMonth == null
              ? 'All-time financial overview'
              : 'Showing ${formatMonth(monthDateOf(_selectedMonth!))}',
          style: const TextStyle(color: TppColors.muted, fontSize: 13),
        ),
        const SizedBox(height: 16),
        SingleChildScrollView(
          scrollDirection: Axis.horizontal,
          child: Row(
            children: [
              FilterChipButton(
                label: 'All Time',
                selected: _selectedMonth == null,
                onTap: () => setState(() => _selectedMonth = null),
              ),
              const SizedBox(width: 8),
              ...months.map(
                (m) => Padding(
                  padding: const EdgeInsets.only(right: 8),
                  child: FilterChipButton(
                    label: formatMonthShort(monthDateOf(m)),
                    selected: _selectedMonth == m,
                    onTap: () => setState(() => _selectedMonth = m),
                  ),
                ),
              ),
            ],
          ),
        ),
        const SizedBox(height: 18),
        GridView(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: wide ? 3 : 2,
            crossAxisSpacing: 12,
            mainAxisSpacing: 12,
            mainAxisExtent: 116,
          ),
          children: [
            StatCard(
              label: 'In-Stock Value',
              value: formatMoney(summary.inStockValue),
              subtitle: '${summary.inStockCount} in stock',
            ),
            StatCard(
              label: 'Revenue',
              value: formatMoney(summary.revenue),
              subtitle: '${summary.soldCount} sold',
              accent: TppColors.warn,
            ),
            StatCard(
              label: 'Total Profit',
              value: formatMoney(summary.totalProfit),
            ),
            StatCard(
              label: "Asif's Profit",
              value: formatMoney(summary.asifProfit),
            ),
            StatCard(
              label: "Shozab's Profit",
              value: formatMoney(summary.shozabProfit),
              accent: TppColors.warn,
            ),
            StatCard(
              label: 'Sold Phones',
              value: '${summary.soldCount}',
              accent: TppColors.warn,
            ),
          ],
        ),
        const SizedBox(height: 20),
        _panel(
          title: 'Month-wise Breakdown',
          child: breakdown.isEmpty
              ? const Text(
                  'No sales recorded yet',
                  style: TextStyle(color: TppColors.muted),
                )
              : Column(
                  children: [
                    for (var i = 0; i < breakdown.length; i++)
                      _MonthRow(
                        entry: breakdown[i],
                        selected: _selectedMonth == breakdown[i].key,
                        last: i == breakdown.length - 1,
                        onTap: () => setState(
                          () => _selectedMonth = _selectedMonth == breakdown[i].key
                              ? null
                              : breakdown[i].key,
                        ),
                      ),
                  ],
                ),
        ),
      ],
    );
  }

  Widget _panel({required String title, required Widget child}) {
    return Container(
      width: double.infinity,
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: TppColors.bgPanel,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: TppColors.line),
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        children: [
          Text(
            title,
            style: const TextStyle(
              fontSize: 16,
              fontWeight: FontWeight.w700,
              color: TppColors.textPrimary,
            ),
          ),
          const SizedBox(height: 14),
          child,
        ],
      ),
    );
  }
}

class _MonthRow extends StatelessWidget {
  const _MonthRow({
    required this.entry,
    required this.selected,
    required this.last,
    required this.onTap,
  });

  final MonthlyEntry entry;
  final bool selected;
  final bool last;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: EdgeInsets.only(bottom: last ? 0 : 10),
      child: Material(
        color: selected ? TppColors.accentDim : TppColors.bgElevated,
        borderRadius: BorderRadius.circular(12),
        child: InkWell(
          onTap: onTap,
          borderRadius: BorderRadius.circular(12),
          child: Container(
            padding: const EdgeInsets.all(14),
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(12),
              border: Border.all(
                color: selected
                    ? TppColors.accent.withValues(alpha: 0.5)
                    : TppColors.line,
              ),
            ),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Expanded(
                      child: Text(
                        formatMonth(entry.month),
                        style: const TextStyle(
                          fontWeight: FontWeight.w700,
                          color: TppColors.textPrimary,
                          fontSize: 15,
                        ),
                      ),
                    ),
                    Container(
                      padding: const EdgeInsets.symmetric(
                          horizontal: 8, vertical: 3),
                      decoration: BoxDecoration(
                        color: TppColors.bg,
                        borderRadius: BorderRadius.circular(999),
                      ),
                      child: Text(
                        '${entry.soldCount} sold',
                        style: const TextStyle(
                          color: TppColors.muted,
                          fontSize: 11,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                  ],
                ),
                const SizedBox(height: 10),
                Wrap(
                  spacing: 18,
                  runSpacing: 8,
                  children: [
                    _metric('Revenue', formatMoney(entry.revenue),
                        TppColors.warn),
                    _metric('Profit', formatMoney(entry.totalProfit),
                        TppColors.accent),
                    _metric('Asif', formatMoney(entry.asifProfit),
                        TppColors.textSecondary),
                    _metric('Shozab', formatMoney(entry.shozabProfit),
                        TppColors.textSecondary),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }

  Widget _metric(String label, String value, Color color) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      mainAxisSize: MainAxisSize.min,
      children: [
        Text(
          label,
          style: const TextStyle(color: TppColors.muted, fontSize: 11),
        ),
        const SizedBox(height: 2),
        Text(
          value,
          style: TextStyle(
            color: color,
            fontSize: 13,
            fontWeight: FontWeight.w700,
          ),
        ),
      ],
    );
  }
}
