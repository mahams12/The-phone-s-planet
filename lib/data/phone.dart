import 'package:flutter/material.dart';

class Phone {
  const Phone({
    this.id = '',
    this.company = '',
    this.model = '',
    this.detail = '',
    this.imei1 = '',
    this.imei2 = '',
    this.type = 'JV',
    this.pta = 'Non PTA',
    this.storage = '',
    this.colour = '',
    this.purchasePrice = 0,
    this.salePrice = 0,
    this.toldPrice = 0,
    this.commissionFromAsif = 0,
    this.withdrawn = 0,
    this.sold = false,
    this.createdAt = 0,
    this.datePurchased = 0,
    this.dateSold = 0,
    this.batteryHealth = 0,
  });

  final String id;
  final String company;
  final String model;
  final String detail;
  final String imei1;
  final String imei2;
  final String type;
  final String pta;
  final String storage;
  final String colour;
  final double purchasePrice;

  /// Actual sale price charged to the customer.
  final double salePrice;

  /// Sale price told to Asif (may be lower than [salePrice]).
  final double toldPrice;

  /// Commission Asif pays Shozab for selling the phone.
  final double commissionFromAsif;

  final double withdrawn;
  final bool sold;
  final int createdAt;

  /// Epoch millis for when the phone was purchased (0 = unset).
  final int datePurchased;

  /// Epoch millis for when the phone was sold (0 = unset).
  final int dateSold;

  /// Battery health percentage 0-100 (0 = unset).
  final int batteryHealth;

  Phone copyWith({
    String? id,
    String? company,
    String? model,
    String? detail,
    String? imei1,
    String? imei2,
    String? type,
    String? pta,
    String? storage,
    String? colour,
    double? purchasePrice,
    double? salePrice,
    double? toldPrice,
    double? commissionFromAsif,
    double? withdrawn,
    bool? sold,
    int? createdAt,
    int? datePurchased,
    int? dateSold,
    int? batteryHealth,
  }) {
    return Phone(
      id: id ?? this.id,
      company: company ?? this.company,
      model: model ?? this.model,
      detail: detail ?? this.detail,
      imei1: imei1 ?? this.imei1,
      imei2: imei2 ?? this.imei2,
      type: type ?? this.type,
      pta: pta ?? this.pta,
      storage: storage ?? this.storage,
      colour: colour ?? this.colour,
      purchasePrice: purchasePrice ?? this.purchasePrice,
      salePrice: salePrice ?? this.salePrice,
      toldPrice: toldPrice ?? this.toldPrice,
      commissionFromAsif: commissionFromAsif ?? this.commissionFromAsif,
      withdrawn: withdrawn ?? this.withdrawn,
      sold: sold ?? this.sold,
      createdAt: createdAt ?? this.createdAt,
      datePurchased: datePurchased ?? this.datePurchased,
      dateSold: dateSold ?? this.dateSold,
      batteryHealth: batteryHealth ?? this.batteryHealth,
    );
  }

  Map<String, dynamic> toMap() => {
        'company': company,
        'model': model,
        'detail': detail,
        'imei1': imei1,
        'imei2': imei2,
        'type': type,
        'pta': pta,
        'storage': storage,
        'colour': colour,
        'purchasePrice': purchasePrice,
        'salePrice': salePrice,
        'toldPrice': toldPrice,
        'commissionFromAsif': commissionFromAsif,
        'withdrawn': withdrawn,
        'sold': sold,
        'createdAt': createdAt,
        'datePurchased': datePurchased,
        'dateSold': dateSold,
        'batteryHealth': batteryHealth,
      };

  factory Phone.fromMap(String id, Map<String, dynamic> data) {
    return Phone(
      id: id,
      company: (data['company'] as String?) ?? '',
      model: (data['model'] as String?) ?? '',
      detail: (data['detail'] as String?) ?? '',
      imei1: (data['imei1'] as String?) ?? '',
      imei2: (data['imei2'] as String?) ?? '',
      type: (data['type'] as String?) ?? 'JV',
      pta: (data['pta'] as String?) ?? 'Non PTA',
      storage: (data['storage'] as String?) ?? '',
      colour: (data['colour'] as String?) ?? '',
      purchasePrice: _asDouble(data['purchasePrice']),
      salePrice: _asDouble(data['salePrice']),
      toldPrice: _asDouble(data['toldPrice']),
      commissionFromAsif: _asDouble(data['commissionFromAsif']),
      withdrawn: _asDouble(data['withdrawn']),
      sold: (data['sold'] as bool?) ?? false,
      createdAt: (data['createdAt'] as num?)?.toInt() ??
          DateTime.now().millisecondsSinceEpoch,
      datePurchased: (data['datePurchased'] as num?)?.toInt() ?? 0,
      dateSold: (data['dateSold'] as num?)?.toInt() ?? 0,
      batteryHealth: (data['batteryHealth'] as num?)?.toInt() ?? 0,
    );
  }
}

double _asDouble(dynamic value) {
  if (value is num) return value.toDouble();
  return 0;
}

class PhoneComputed {
  const PhoneComputed({
    required this.sold,
    required this.purchase,
    required this.sale,
    required this.told,
    required this.commissionFromAsif,
    required this.totalMoney,
    required this.totalProfit,
    required this.hiddenProfit,
    required this.asifProfit,
    required this.shozabProfit,
    required this.hasNegativeProfit,
  });

  final bool sold;
  final double purchase;
  final double sale;
  final double told;
  final double commissionFromAsif;
  final double totalMoney;
  final double totalProfit;

  /// Gap between actual sale and what Asif was told.
  final double hiddenProfit;

  final double asifProfit;
  final double shozabProfit;

  /// True when any profit component is negative (loss) — UI should warn.
  final bool hasNegativeProfit;
}

class PhoneTotals {
  const PhoneTotals({
    this.count = 0,
    this.inStock = 0,
    this.sold = 0,
    this.totalMoney = 0,
    this.totalProfit = 0,
    this.asifProfit = 0,
    this.shozabProfit = 0,
  });

  final int count;
  final int inStock;
  final int sold;
  final double totalMoney;
  final double totalProfit;
  final double asifProfit;
  final double shozabProfit;
}

PhoneComputed computePhone(Phone phone) {
  final sold = phone.sold;
  final purchase = phone.purchasePrice;
  // actualSalePrice
  final sale = phone.salePrice;
  // toldToAsif
  final told = phone.toldPrice;
  final commission = phone.commissionFromAsif;

  if (!sold) {
    return PhoneComputed(
      sold: false,
      purchase: purchase,
      sale: sale,
      told: told,
      commissionFromAsif: commission,
      totalMoney: 0,
      totalProfit: 0,
      hiddenProfit: 0,
      asifProfit: 0,
      shozabProfit: 0,
      hasNegativeProfit: false,
    );
  }

  final totalMoney = sale;

  // totalProfit = actualSalePrice - purchasePrice
  final totalProfit = sale - purchase;

  late final double hiddenProfit;
  late final double asifProfit;
  late final double shozabProfit;

  if (told > 0) {
    // hiddenProfit = actualSalePrice - toldToAsif
    hiddenProfit = sale - told;
    // asifProfit = toldToAsif - purchasePrice
    asifProfit = told - purchase;
    // shozabProfit = hiddenProfit + commissionFromAsif
    shozabProfit = hiddenProfit + commission;
  } else {
    // No told price: Asif has no share; Shozab keeps the full sale margin + commission.
    hiddenProfit = 0;
    asifProfit = 0;
    shozabProfit = totalProfit + commission;
  }

  final hasNegativeProfit = totalProfit < 0 ||
      hiddenProfit < 0 ||
      asifProfit < 0 ||
      shozabProfit < 0;

  return PhoneComputed(
    sold: true,
    purchase: purchase,
    sale: sale,
    told: told,
    commissionFromAsif: commission,
    totalMoney: totalMoney,
    totalProfit: totalProfit,
    hiddenProfit: hiddenProfit,
    asifProfit: asifProfit,
    shozabProfit: shozabProfit,
    hasNegativeProfit: hasNegativeProfit,
  );
}

PhoneTotals computeAll(List<Phone> phones) {
  var inStock = 0;
  var sold = 0;
  var totalMoney = 0.0;
  var totalProfit = 0.0;
  var asifProfit = 0.0;
  var shozabProfit = 0.0;

  for (final phone in phones) {
    final c = computePhone(phone);
    if (c.sold) {
      sold++;
    } else {
      inStock++;
    }
    totalMoney += c.totalMoney;
    totalProfit += c.totalProfit;
    asifProfit += c.asifProfit;
    shozabProfit += c.shozabProfit;
  }

  return PhoneTotals(
    count: phones.length,
    inStock: inStock,
    sold: sold,
    totalMoney: totalMoney,
    totalProfit: totalProfit,
    asifProfit: asifProfit,
    shozabProfit: shozabProfit,
  );
}

Color brandColor(String company) {
  switch (company.trim().toLowerCase()) {
    case 'apple':
      return const Color(0xFFD6D6D6);
    case 'samsung':
      return const Color(0xFF4C8DFF);
    case 'oppo':
      return const Color(0xFF34C38F);
    case 'vivo':
      return const Color(0xFFA78BFA);
    case 'oneplus':
      return const Color(0xFFFB7185);
    default:
      return const Color(0xFFF5B14C);
  }
}
