import 'package:flutter/material.dart';

import '../data/phone.dart';
import '../theme/colors.dart';
import '../util/formatters.dart';
import '../widgets/common.dart';

class DashboardScreen extends StatelessWidget {
  const DashboardScreen({
    super.key,
    required this.phones,
    required this.totals,
    required this.onOpenPhone,
  });

  final List<Phone> phones;
  final PhoneTotals totals;
  final ValueChanged<Phone> onOpenPhone;

  @override
  Widget build(BuildContext context) {
    final recent = [...phones]
      ..sort((a, b) => b.createdAt.compareTo(a.createdAt));
    final topRecent = recent.take(5).toList();

    final brandCounts = <String, int>{};
    for (final p in phones) {
      final key = p.company.trim().isEmpty ? 'Other' : p.company;
      brandCounts[key] = (brandCounts[key] ?? 0) + 1;
    }
    final brands = brandCounts.entries.toList()
      ..sort((a, b) => b.value.compareTo(a.value));
    final maxBrand = brands.isEmpty ? 1 : brands.first.value;

    final asif = totals.asifProfit;
    final shozab = totals.shozabProfit;
    final splitTotal = asif + shozab;
    final asifRatio = splitTotal <= 0 ? 0.5 : asif / splitTotal;

    return ListView(
      padding: const EdgeInsets.only(bottom: 32),
      children: [
        GridView(
          shrinkWrap: true,
          physics: const NeverScrollableScrollPhysics(),
          gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
            crossAxisCount: MediaQuery.sizeOf(context).width > 700 ? 4 : 2,
            crossAxisSpacing: 12,
            mainAxisSpacing: 12,
            mainAxisExtent: 116,
          ),
          children: [
            StatCard(
              label: 'Inventory',
              value: '${totals.count}',
              subtitle: '${totals.inStock} in stock',
            ),
            StatCard(
              label: 'Sold',
              value: '${totals.sold}',
              accent: TppColors.warn,
            ),
            StatCard(
              label: 'Total Profit',
              value: formatMoney(totals.totalProfit),
            ),
            StatCard(
              label: "Shozab's Profit",
              value: formatMoney(totals.shozabProfit),
              accent: TppColors.warn,
            ),
          ],
        ),
        const SizedBox(height: 20),
        _panel(
          title: 'Profit Split',
          child: Column(
            crossAxisAlignment: CrossAxisAlignment.start,
            children: [
              ClipRRect(
                borderRadius: BorderRadius.circular(999),
                child: SizedBox(
                  height: 12,
                  child: Row(
                    children: [
                      Expanded(
                        flex: (asifRatio * 1000).round().clamp(1, 999),
                        child: Container(color: TppColors.accent),
                      ),
                      Expanded(
                        flex: ((1 - asifRatio) * 1000).round().clamp(1, 999),
                        child: Container(color: TppColors.warn),
                      ),
                    ],
                  ),
                ),
              ),
              const SizedBox(height: 14),
              Row(
                children: [
                  _legend('Asif', formatMoney(asif), TppColors.accent),
                  const SizedBox(width: 20),
                  _legend('Shozab', formatMoney(shozab), TppColors.warn),
                ],
              ),
              const SizedBox(height: 8),
              Text(
                'Sales revenue  ${formatMoney(totals.totalMoney)}',
                style: const TextStyle(color: TppColors.muted, fontSize: 12),
              ),
            ],
          ),
        ),
        const SizedBox(height: 16),
        _panel(
          title: 'Inventory by Brand',
          child: brands.isEmpty
              ? const Text(
                  'No phones yet',
                  style: TextStyle(color: TppColors.muted),
                )
              : Column(
                  children: brands.map((e) {
                    final ratio = e.value / maxBrand;
                    return Padding(
                      padding: const EdgeInsets.only(bottom: 10),
                      child: Row(
                        children: [
                          SizedBox(
                            width: 78,
                            child: Text(
                              e.key,
                              overflow: TextOverflow.ellipsis,
                              style: const TextStyle(
                                color: TppColors.textSecondary,
                                fontSize: 12,
                                fontWeight: FontWeight.w600,
                              ),
                            ),
                          ),
                          Expanded(
                            child: ClipRRect(
                              borderRadius: BorderRadius.circular(999),
                              child: LinearProgressIndicator(
                                value: ratio,
                                minHeight: 8,
                                backgroundColor: TppColors.bg,
                                color: brandColor(e.key),
                              ),
                            ),
                          ),
                          const SizedBox(width: 10),
                          Text(
                            '${e.value}',
                            style: const TextStyle(
                              color: TppColors.textPrimary,
                              fontWeight: FontWeight.w700,
                            ),
                          ),
                        ],
                      ),
                    );
                  }).toList(),
                ),
        ),
        const SizedBox(height: 16),
        _panel(
          title: 'Recent Additions',
          child: topRecent.isEmpty
              ? const Text(
                  'Nothing added yet',
                  style: TextStyle(color: TppColors.muted),
                )
              : Material(
                  type: MaterialType.transparency,
                  child: Column(
                    children: topRecent.map((phone) {
                      return ListTile(
                        contentPadding: EdgeInsets.zero,
                        onTap: () => onOpenPhone(phone),
                        leading: CircleAvatar(
                          backgroundColor: brandColor(phone.company)
                              .withValues(alpha: 0.2),
                          child: Icon(
                            Icons.phone_iphone,
                            color: brandColor(phone.company),
                            size: 18,
                          ),
                        ),
                        title: Text(
                          phone.model,
                          style: const TextStyle(fontWeight: FontWeight.w700),
                        ),
                        subtitle: Text(
                          [
                            phone.company,
                            if (phone.storage.isNotEmpty) phone.storage,
                          ].join(' · '),
                          style: const TextStyle(color: TppColors.muted),
                        ),
                        trailing: StatusPill(sold: phone.sold),
                      );
                    }).toList(),
                  ),
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

  Widget _legend(String name, String amount, Color color) {
    return Row(
      children: [
        Container(
          width: 10,
          height: 10,
          decoration: BoxDecoration(color: color, shape: BoxShape.circle),
        ),
        const SizedBox(width: 6),
        Text(
          '$name  $amount',
          style: const TextStyle(
            color: TppColors.textSecondary,
            fontSize: 13,
            fontWeight: FontWeight.w600,
          ),
        ),
      ],
    );
  }
}
