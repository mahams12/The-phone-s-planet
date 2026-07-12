import 'phone.dart';

/// A month bucket key in the form "YYYY-MM" derived from an epoch millis value.
/// Returns null when the timestamp is unset (0).
String? monthKeyOf(int millis) {
  if (millis <= 0) return null;
  final d = DateTime.fromMillisecondsSinceEpoch(millis);
  final mm = d.month.toString().padLeft(2, '0');
  return '${d.year}-$mm';
}

DateTime monthDateOf(String key) {
  final parts = key.split('-');
  return DateTime(int.parse(parts[0]), int.parse(parts[1]));
}

/// Aggregated financials for a filtered set of phones.
class BudgetSummary {
  const BudgetSummary({
    this.inStockValue = 0,
    this.revenue = 0,
    this.totalProfit = 0,
    this.asifProfit = 0,
    this.shozabProfit = 0,
    this.soldCount = 0,
    this.inStockCount = 0,
  });

  final double inStockValue;
  final double revenue;
  final double totalProfit;
  final double asifProfit;
  final double shozabProfit;
  final int soldCount;
  final int inStockCount;
}

/// A single month's rolled-up sales figures (grouped by date sold).
class MonthlyEntry {
  const MonthlyEntry({
    required this.key,
    required this.month,
    this.revenue = 0,
    this.totalProfit = 0,
    this.asifProfit = 0,
    this.shozabProfit = 0,
    this.soldCount = 0,
  });

  final String key;
  final DateTime month;
  final double revenue;
  final double totalProfit;
  final double asifProfit;
  final double shozabProfit;
  final int soldCount;
}

/// All month keys that have activity, derived from both sold dates and
/// purchase dates, sorted most-recent first.
List<String> availableMonths(List<Phone> phones) {
  final set = <String>{};
  for (final p in phones) {
    final sold = monthKeyOf(p.dateSold);
    final bought = monthKeyOf(p.datePurchased);
    if (sold != null) set.add(sold);
    if (bought != null) set.add(bought);
  }
  final list = set.toList()..sort((a, b) => b.compareTo(a));
  return list;
}

/// Computes a budget summary. When [monthKey] is null the totals cover
/// everything; otherwise revenue/profit are limited to phones sold in that
/// month and in-stock value to phones purchased in that month.
BudgetSummary budgetForMonth(List<Phone> phones, String? monthKey) {
  var inStockValue = 0.0;
  var revenue = 0.0;
  var totalProfit = 0.0;
  var asifProfit = 0.0;
  var shozabProfit = 0.0;
  var soldCount = 0;
  var inStockCount = 0;

  for (final p in phones) {
    final c = computePhone(p);
    if (c.sold) {
      if (monthKey != null && monthKeyOf(p.dateSold) != monthKey) continue;
      revenue += c.totalMoney;
      totalProfit += c.totalProfit;
      asifProfit += c.asifProfit;
      shozabProfit += c.shozabProfit;
      soldCount++;
    } else {
      if (monthKey != null && monthKeyOf(p.datePurchased) != monthKey) continue;
      inStockValue += p.purchasePrice;
      inStockCount++;
    }
  }

  return BudgetSummary(
    inStockValue: inStockValue,
    revenue: revenue,
    totalProfit: totalProfit,
    asifProfit: asifProfit,
    shozabProfit: shozabProfit,
    soldCount: soldCount,
    inStockCount: inStockCount,
  );
}

/// Month-by-month breakdown of sold phones, most recent month first.
List<MonthlyEntry> monthlyBreakdown(List<Phone> phones) {
  final buckets = <String, List<Phone>>{};
  for (final p in phones) {
    if (!p.sold) continue;
    final key = monthKeyOf(p.dateSold);
    if (key == null) continue;
    buckets.putIfAbsent(key, () => []).add(p);
  }

  final entries = <MonthlyEntry>[];
  for (final key in buckets.keys) {
    var revenue = 0.0;
    var totalProfit = 0.0;
    var asifProfit = 0.0;
    var shozabProfit = 0.0;
    for (final p in buckets[key]!) {
      final c = computePhone(p);
      revenue += c.totalMoney;
      totalProfit += c.totalProfit;
      asifProfit += c.asifProfit;
      shozabProfit += c.shozabProfit;
    }
    entries.add(MonthlyEntry(
      key: key,
      month: monthDateOf(key),
      revenue: revenue,
      totalProfit: totalProfit,
      asifProfit: asifProfit,
      shozabProfit: shozabProfit,
      soldCount: buckets[key]!.length,
    ));
  }

  entries.sort((a, b) => b.key.compareTo(a.key));
  return entries;
}
