import 'package:flutter_test/flutter_test.dart';

import 'package:the_phones_planet/data/phone.dart';

void main() {
  test('computePhone splits profit with told price and commission', () {
    final phone = Phone(
      model: 'iPhone 13',
      purchasePrice: 100000,
      salePrice: 130000,
      toldPrice: 120000,
      commissionFromAsif: 5000,
      sold: true,
    );
    final c = computePhone(phone);
    // totalProfit = actualSalePrice - purchasePrice
    expect(c.totalMoney, 130000);
    expect(c.totalProfit, 30000);
    // hiddenProfit = actualSalePrice - toldToAsif
    expect(c.hiddenProfit, 10000);
    // asifProfit = toldToAsif - purchasePrice
    expect(c.asifProfit, 20000);
    // shozabProfit = hiddenProfit + commissionFromAsif
    expect(c.shozabProfit, 15000);
    expect(c.hasNegativeProfit, false);
  });

  test('unsold phones contribute zero profits', () {
    final phone = Phone(
      model: 'S23',
      purchasePrice: 80000,
      salePrice: 90000,
      toldPrice: 85000,
      commissionFromAsif: 1000,
      sold: false,
    );
    final c = computePhone(phone);
    expect(c.totalMoney, 0);
    expect(c.totalProfit, 0);
    expect(c.hiddenProfit, 0);
    expect(c.asifProfit, 0);
    expect(c.shozabProfit, 0);
    expect(c.hasNegativeProfit, false);
  });

  test('negative profits set warning flag without clamping', () {
    final phone = Phone(
      model: 'Lossy',
      purchasePrice: 100000,
      salePrice: 90000,
      toldPrice: 95000,
      commissionFromAsif: 0,
      sold: true,
    );
    final c = computePhone(phone);
    expect(c.totalProfit, -10000);
    expect(c.hiddenProfit, -5000);
    expect(c.asifProfit, -5000);
    expect(c.shozabProfit, -5000);
    expect(c.hasNegativeProfit, true);
  });
}
