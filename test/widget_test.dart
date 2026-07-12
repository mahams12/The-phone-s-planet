import 'package:flutter_test/flutter_test.dart';

import 'package:the_phones_planet/data/phone.dart';

void main() {
  test('computePhone splits partner profit when sold with told price', () {
    final phone = Phone(
      model: 'iPhone 13',
      purchasePrice: 100000,
      salePrice: 130000,
      toldPrice: 120000,
      sold: true,
    );
    final c = computePhone(phone);
    expect(c.totalMoney, 130000);
    expect(c.totalProfit, 30000);
    expect(c.asifProfit, 10000);
    expect(c.shozabProfit, 20000);
  });

  test('unsold phones contribute zero profits', () {
    final phone = Phone(
      model: 'S23',
      purchasePrice: 80000,
      salePrice: 90000,
      toldPrice: 85000,
      sold: false,
    );
    final c = computePhone(phone);
    expect(c.totalMoney, 0);
    expect(c.totalProfit, 0);
    expect(c.asifProfit, 0);
    expect(c.shozabProfit, 0);
  });
}
