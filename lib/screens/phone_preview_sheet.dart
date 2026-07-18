import 'package:flutter/material.dart';

import '../data/phone.dart';
import '../theme/colors.dart';
import '../util/formatters.dart';
import '../widgets/common.dart';

Future<void> showPhonePreviewSheet({
  required BuildContext context,
  required Phone phone,
  required VoidCallback onEdit,
  required VoidCallback onDelete,
}) {
  final computed = computePhone(phone);
  final narrow = MediaQuery.sizeOf(context).width < 600;

  Widget body() {
    return Padding(
      padding: const EdgeInsets.fromLTRB(20, 12, 20, 24),
      child: Column(
        mainAxisSize: MainAxisSize.min,
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Row(
            children: [
              Expanded(
                child: Column(
                  crossAxisAlignment: CrossAxisAlignment.start,
                  children: [
                    Text(
                      phone.model,
                      style: const TextStyle(
                        fontSize: 22,
                        fontWeight: FontWeight.w800,
                      ),
                    ),
                    const SizedBox(height: 4),
                    Text(
                      [
                        phone.company,
                        if (phone.storage.isNotEmpty) phone.storage,
                        if (phone.colour.isNotEmpty) phone.colour,
                      ].join(' · '),
                      style: const TextStyle(color: TppColors.textSecondary),
                    ),
                  ],
                ),
              ),
              StatusPill(sold: phone.sold),
              IconButton(
                onPressed: () => Navigator.pop(context),
                icon: const Icon(Icons.close),
              ),
            ],
          ),
          const SizedBox(height: 16),
          _section(
            'Device',
            [
              _kv('Type', phone.type),
              _kv('PTA', phone.pta),
              if (phone.batteryHealth > 0)
                _kv('Battery Health', '${phone.batteryHealth}%'),
              if (phone.imei1.isNotEmpty) _kv('IMEI 1', phone.imei1),
              if (phone.imei2.isNotEmpty) _kv('IMEI 2', phone.imei2),
              if (phone.detail.isNotEmpty) _kv('Detail', phone.detail),
            ],
          ),
          const SizedBox(height: 12),
          _section(
            'Sales Ledger',
            [
              _kv('Purchase', formatMoney(phone.purchasePrice)),
              _kv('Actual Sale', formatMoneyOrDash(phone.salePrice, phone.sold)),
              _kv('Told to Asif', formatMoney(phone.toldPrice)),
              _kv('Commission from Asif', formatMoney(phone.commissionFromAsif)),
              if (phone.datePurchased > 0)
                _kv('Date Purchased', formatDate(phone.datePurchased)),
              if (phone.sold && phone.dateSold > 0)
                _kv('Date Sold', formatDate(phone.dateSold)),
              _kv('Total Money', formatMoneyOrDash(computed.totalMoney, phone.sold)),
              _kv('Total Profit', formatMoneyOrDash(computed.totalProfit, phone.sold)),
              _kv('Hidden Profit', formatMoneyOrDash(computed.hiddenProfit, phone.sold)),
              _kv("Asif's Profit", formatMoneyOrDash(computed.asifProfit, phone.sold)),
              _kv("Shozab's Profit", formatMoneyOrDash(computed.shozabProfit, phone.sold)),
              if (computed.hasNegativeProfit)
                const Padding(
                  padding: EdgeInsets.only(top: 4),
                  child: Text(
                    'Warning: one or more profit values are negative (loss).',
                    style: TextStyle(
                      color: TppColors.danger,
                      fontSize: 12,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
            ],
          ),
          const SizedBox(height: 20),
          Row(
            children: [
              Expanded(
                child: TppDangerButton(
                  label: 'Delete',
                  onPressed: () {
                    Navigator.pop(context);
                    onDelete();
                  },
                ),
              ),
              const SizedBox(width: 10),
              Expanded(
                child: TppGhostButton(
                  label: 'Close',
                  onPressed: () => Navigator.pop(context),
                ),
              ),
              const SizedBox(width: 10),
              Expanded(
                child: TppPrimaryButton(
                  label: 'Edit',
                  onPressed: () {
                    Navigator.pop(context);
                    onEdit();
                  },
                ),
              ),
            ],
          ),
        ],
      ),
    );
  }

  if (narrow) {
    return showDialog(
      context: context,
      builder: (context) => Dialog(
        backgroundColor: TppColors.bgPanel,
        insetPadding: const EdgeInsets.all(16),
        child: SingleChildScrollView(child: body()),
      ),
    );
  }

  return showModalBottomSheet(
    context: context,
    isScrollControlled: true,
    backgroundColor: TppColors.bgPanel,
    shape: const RoundedRectangleBorder(
      borderRadius: BorderRadius.vertical(top: Radius.circular(20)),
    ),
    builder: (context) => SafeArea(
      child: SingleChildScrollView(child: body()),
    ),
  );
}

Widget _section(String title, List<Widget> children) {
  return Container(
    width: double.infinity,
    padding: const EdgeInsets.all(14),
    decoration: BoxDecoration(
      color: TppColors.bgElevated,
      borderRadius: BorderRadius.circular(14),
      border: Border.all(color: TppColors.line),
    ),
    child: Column(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        Text(
          title,
          style: const TextStyle(
            fontWeight: FontWeight.w700,
            color: TppColors.textPrimary,
          ),
        ),
        const SizedBox(height: 10),
        ...children,
      ],
    ),
  );
}

Widget _kv(String key, String value) {
  return Padding(
    padding: const EdgeInsets.only(bottom: 8),
    child: Row(
      crossAxisAlignment: CrossAxisAlignment.start,
      children: [
        SizedBox(
          width: 120,
          child: Text(
            key,
            style: const TextStyle(color: TppColors.muted, fontSize: 13),
          ),
        ),
        Expanded(
          child: Text(
            value,
            style: const TextStyle(
              color: TppColors.textPrimary,
              fontWeight: FontWeight.w600,
              fontSize: 13,
            ),
          ),
        ),
      ],
    ),
  );
}
