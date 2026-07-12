import 'package:flutter/material.dart';
import 'package:flutter/services.dart';

import '../data/phone.dart';
import '../theme/colors.dart';
import '../util/formatters.dart';
import 'common.dart';

const _minPickerDate = 2000;

const companySuggestions = ['Apple', 'Samsung', 'Oppo', 'Vivo', 'OnePlus'];
const typeOptions = ['JV', 'Factory Unlock', 'Bypass'];
const ptaOptions = ['Non PTA', 'Approved'];

class PhoneFormData {
  PhoneFormData({
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
    this.sold = false,
    this.createdAt = 0,
    this.datePurchased = 0,
    this.dateSold = 0,
    this.batteryHealth = 0,
  });

  factory PhoneFormData.fromPhone(Phone phone) {
    return PhoneFormData(
      id: phone.id,
      company: phone.company,
      model: phone.model,
      detail: phone.detail,
      imei1: phone.imei1,
      imei2: phone.imei2,
      type: phone.type,
      pta: phone.pta,
      storage: phone.storage,
      colour: phone.colour,
      purchasePrice: phone.purchasePrice,
      salePrice: phone.salePrice,
      toldPrice: phone.toldPrice,
      sold: phone.sold,
      createdAt: phone.createdAt,
      datePurchased: phone.datePurchased,
      dateSold: phone.dateSold,
      batteryHealth: phone.batteryHealth,
    );
  }

  String id;
  String company;
  String model;
  String detail;
  String imei1;
  String imei2;
  String type;
  String pta;
  String storage;
  String colour;
  double purchasePrice;
  double salePrice;
  double toldPrice;
  bool sold;
  int createdAt;
  int datePurchased;
  int dateSold;
  int batteryHealth;

  Phone toPhone() {
    final companyTrim = company.trim();
    return Phone(
      id: id,
      company: companyTrim.isEmpty ? 'Other' : companyTrim,
      model: model.trim(),
      detail: detail.trim(),
      imei1: imei1.trim(),
      imei2: imei2.trim(),
      type: type,
      pta: pta,
      storage: storage.trim(),
      colour: colour.trim(),
      purchasePrice: purchasePrice,
      salePrice: salePrice,
      toldPrice: toldPrice,
      withdrawn: 0,
      sold: sold,
      createdAt: createdAt,
      datePurchased: datePurchased,
      dateSold: sold ? dateSold : 0,
      batteryHealth: batteryHealth.clamp(0, 100),
    );
  }

  PhoneComputed get computed => computePhone(toPhone());
}

class PhoneFormFields extends StatefulWidget {
  const PhoneFormFields({
    super.key,
    required this.data,
    this.onChanged,
  });

  final PhoneFormData data;
  final VoidCallback? onChanged;

  @override
  State<PhoneFormFields> createState() => _PhoneFormFieldsState();
}

class _PhoneFormFieldsState extends State<PhoneFormFields> {
  // A single lightweight notifier drives only the widgets that depend on
  // derived state (chips, dropdowns, IMEI counters, sold switch, preview),
  // so typing never rebuilds the whole form or recreates text fields.
  final ValueNotifier<int> _rev = ValueNotifier<int>(0);

  late final TextEditingController _company;
  late final TextEditingController _model;
  late final TextEditingController _detail;
  late final TextEditingController _storage;
  late final TextEditingController _colour;
  late final TextEditingController _imei1;
  late final TextEditingController _imei2;
  late final TextEditingController _purchase;
  late final TextEditingController _sale;
  late final TextEditingController _told;
  late final TextEditingController _battery;

  PhoneFormData get data => widget.data;

  @override
  void initState() {
    super.initState();
    _company = TextEditingController(text: data.company);
    _model = TextEditingController(text: data.model);
    _detail = TextEditingController(text: data.detail);
    _storage = TextEditingController(text: data.storage);
    _colour = TextEditingController(text: data.colour);
    _imei1 = TextEditingController(text: data.imei1);
    _imei2 = TextEditingController(text: data.imei2);
    _purchase = TextEditingController(text: _moneyText(data.purchasePrice));
    _sale = TextEditingController(text: _moneyText(data.salePrice));
    _told = TextEditingController(text: _moneyText(data.toldPrice));
    _battery = TextEditingController(
      text: data.batteryHealth == 0 ? '' : data.batteryHealth.toString(),
    );
  }

  @override
  void dispose() {
    _rev.dispose();
    _company.dispose();
    _model.dispose();
    _detail.dispose();
    _storage.dispose();
    _colour.dispose();
    _imei1.dispose();
    _imei2.dispose();
    _purchase.dispose();
    _sale.dispose();
    _told.dispose();
    _battery.dispose();
    super.dispose();
  }

  static String _moneyText(double v) => v == 0 ? '' : v.toInt().toString();

  void _bump() {
    _rev.value++;
    widget.onChanged?.call();
  }

  void _setAmount(TextEditingController controller, double value,
      ValueChanged<double> assign) {
    assign(value);
    final text = _moneyText(value);
    controller.value = TextEditingValue(
      text: text,
      selection: TextSelection.collapsed(offset: text.length),
    );
    _bump();
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        _textField(
          controller: _company,
          label: 'Company',
          hint: 'Apple, Samsung…',
          onChanged: (v) {
            data.company = v;
            _bump();
          },
        ),
        const SizedBox(height: 8),
        ValueListenableBuilder<int>(
          valueListenable: _rev,
          builder: (context, _, _) => Wrap(
            spacing: 8,
            runSpacing: 8,
            children: companySuggestions
                .map(
                  (c) => FilterChipButton(
                    label: c,
                    selected: data.company == c,
                    onTap: () {
                      data.company = c;
                      _company.value = TextEditingValue(
                        text: c,
                        selection: TextSelection.collapsed(offset: c.length),
                      );
                      _bump();
                    },
                  ),
                )
                .toList(),
          ),
        ),
        const SizedBox(height: 14),
        _textField(
          controller: _model,
          label: 'Model *',
          hint: 'iPhone 13, S23…',
          onChanged: (v) => data.model = v,
        ),
        const SizedBox(height: 14),
        _textField(
          controller: _detail,
          label: 'Detail / condition',
          hint: 'Battery, scratches…',
          maxLines: 2,
          onChanged: (v) => data.detail = v,
        ),
        const SizedBox(height: 14),
        Row(
          children: [
            Expanded(
              child: _dropdown(
                label: 'Type',
                getValue: () => data.type,
                items: typeOptions,
                onChanged: (v) {
                  data.type = v;
                  _bump();
                },
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: _dropdown(
                label: 'PTA',
                getValue: () => data.pta,
                items: ptaOptions,
                onChanged: (v) {
                  data.pta = v;
                  _bump();
                },
              ),
            ),
          ],
        ),
        const SizedBox(height: 14),
        Row(
          children: [
            Expanded(
              child: _textField(
                controller: _storage,
                label: 'Storage',
                hint: '128GB',
                onChanged: (v) => data.storage = v,
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              child: _textField(
                controller: _colour,
                label: 'Colour',
                hint: 'Black',
                onChanged: (v) => data.colour = v,
              ),
            ),
          ],
        ),
        const SizedBox(height: 14),
        Row(
          crossAxisAlignment: CrossAxisAlignment.start,
          children: [
            Expanded(child: _batteryField()),
            const SizedBox(width: 12),
            Expanded(
              child: _dateField(
                label: 'Date Purchased',
                getValue: () => data.datePurchased,
                onPick: (millis) {
                  data.datePurchased = millis;
                  _bump();
                },
              ),
            ),
          ],
        ),
        const SizedBox(height: 14),
        _imeiField(
          controller: _imei1,
          label: 'IMEI 1',
          onChanged: (v) {
            data.imei1 = v;
            _bump();
          },
        ),
        const SizedBox(height: 14),
        _imeiField(
          controller: _imei2,
          label: 'IMEI 2 (optional)',
          onChanged: (v) {
            data.imei2 = v;
            _bump();
          },
        ),
        const SizedBox(height: 14),
        _amountField(
          controller: _purchase,
          label: 'Purchase price',
          getValue: () => data.purchasePrice,
          onChanged: (v) => data.purchasePrice = v,
        ),
        const SizedBox(height: 14),
        _amountField(
          controller: _sale,
          label: 'Sale price',
          getValue: () => data.salePrice,
          onChanged: (v) => data.salePrice = v,
        ),
        const SizedBox(height: 14),
        _amountField(
          controller: _told,
          label: 'Told to Asif',
          getValue: () => data.toldPrice,
          onChanged: (v) => data.toldPrice = v,
        ),
        const SizedBox(height: 8),
        ValueListenableBuilder<int>(
          valueListenable: _rev,
          builder: (context, _, _) => Column(
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              SwitchListTile.adaptive(
                contentPadding: EdgeInsets.zero,
                title: const Text(
                  'Mark as Sold',
                  style: TextStyle(fontWeight: FontWeight.w600),
                ),
                activeThumbColor: TppColors.accentOn,
                activeTrackColor: TppColors.accent,
                value: data.sold,
                onChanged: (v) {
                  data.sold = v;
                  if (v && data.dateSold == 0) {
                    data.dateSold = DateTime.now().millisecondsSinceEpoch;
                  }
                  _bump();
                },
              ),
              if (data.sold) ...[
                const SizedBox(height: 6),
                _dateField(
                  label: 'Date Sold *',
                  getValue: () => data.dateSold,
                  onPick: (millis) {
                    data.dateSold = millis;
                    _bump();
                  },
                ),
              ],
            ],
          ),
        ),
        const SizedBox(height: 12),
        ValueListenableBuilder<int>(
          valueListenable: _rev,
          builder: (context, _, _) => CalcPreviewPanel(computed: data.computed),
        ),
      ],
    );
  }

  Widget _textField({
    required TextEditingController controller,
    required String label,
    required String hint,
    required ValueChanged<String> onChanged,
    int maxLines = 1,
  }) {
    return TextField(
      controller: controller,
      maxLines: maxLines,
      style: const TextStyle(color: TppColors.textPrimary),
      decoration: InputDecoration(labelText: label, hintText: hint),
      onChanged: onChanged,
    );
  }

  Widget _imeiField({
    required TextEditingController controller,
    required String label,
    required ValueChanged<String> onChanged,
  }) {
    return ValueListenableBuilder<int>(
      valueListenable: _rev,
      builder: (context, _, _) => TextField(
        controller: controller,
        keyboardType: TextInputType.number,
        inputFormatters: [
          FilteringTextInputFormatter.digitsOnly,
          LengthLimitingTextInputFormatter(15),
        ],
        style: const TextStyle(
          color: TppColors.textPrimary,
          fontFamily: 'monospace',
          letterSpacing: 0.5,
        ),
        decoration: InputDecoration(
          labelText: label,
          hintText: '15 digits',
          helperText: '${controller.text.length}/15 digits',
          helperStyle: const TextStyle(color: TppColors.muted),
        ),
        onChanged: onChanged,
      ),
    );
  }

  Widget _amountField({
    required TextEditingController controller,
    required String label,
    required double Function() getValue,
    required ValueChanged<double> onChanged,
  }) {
    return Row(
      children: [
        Expanded(
          child: TextField(
            controller: controller,
            keyboardType: TextInputType.number,
            inputFormatters: [FilteringTextInputFormatter.digitsOnly],
            style: const TextStyle(color: TppColors.textPrimary),
            decoration: InputDecoration(labelText: label, hintText: '0'),
            onChanged: (v) {
              onChanged(double.tryParse(v) ?? 0);
              _bump();
            },
          ),
        ),
        const SizedBox(width: 8),
        _stepButton(
          icon: Icons.remove,
          onTap: () => _setAmount(
            controller,
            (getValue() - 500).clamp(0, double.infinity),
            onChanged,
          ),
        ),
        const SizedBox(width: 6),
        _stepButton(
          icon: Icons.add,
          onTap: () => _setAmount(controller, getValue() + 500, onChanged),
        ),
      ],
    );
  }

  Widget _batteryField() {
    return TextField(
      controller: _battery,
      keyboardType: TextInputType.number,
      inputFormatters: [
        FilteringTextInputFormatter.digitsOnly,
        LengthLimitingTextInputFormatter(3),
      ],
      style: const TextStyle(color: TppColors.textPrimary),
      decoration: const InputDecoration(
        labelText: 'Battery Health',
        hintText: '0-100',
        suffixText: '%',
      ),
      onChanged: (v) {
        final parsed = int.tryParse(v) ?? 0;
        data.batteryHealth = parsed.clamp(0, 100);
      },
    );
  }

  Widget _dateField({
    required String label,
    required int Function() getValue,
    required ValueChanged<int> onPick,
  }) {
    final millis = getValue();
    final hasDate = millis > 0;
    return InkWell(
      borderRadius: BorderRadius.circular(12),
      onTap: () async {
        final now = DateTime.now();
        final initial = hasDate
            ? DateTime.fromMillisecondsSinceEpoch(millis)
            : now;
        final picked = await showDatePicker(
          context: context,
          initialDate: initial,
          firstDate: DateTime(_minPickerDate),
          lastDate: DateTime(now.year + 1, 12, 31),
          builder: (context, child) => Theme(
            data: Theme.of(context).copyWith(
              colorScheme: const ColorScheme.dark(
                primary: TppColors.accent,
                onPrimary: TppColors.accentOn,
                surface: TppColors.bgPanel,
                onSurface: TppColors.textPrimary,
              ),
              dialogTheme:
                  const DialogThemeData(backgroundColor: TppColors.bgPanel),
            ),
            child: child!,
          ),
        );
        if (picked != null) {
          onPick(picked.millisecondsSinceEpoch);
        }
      },
      child: InputDecorator(
        decoration: InputDecoration(labelText: label),
        child: Row(
          children: [
            const Icon(Icons.calendar_today_outlined,
                size: 16, color: TppColors.muted),
            const SizedBox(width: 8),
            Expanded(
              child: Text(
                hasDate ? formatDate(millis) : 'Select date',
                style: TextStyle(
                  color: hasDate ? TppColors.textPrimary : TppColors.muted,
                  fontSize: 14,
                  fontWeight: FontWeight.w600,
                ),
              ),
            ),
          ],
        ),
      ),
    );
  }

  Widget _stepButton({required IconData icon, required VoidCallback onTap}) {
    return Material(
      color: TppColors.bgElevated,
      borderRadius: BorderRadius.circular(10),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(10),
        child: Container(
          width: 42,
          height: 48,
          alignment: Alignment.center,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(10),
            border: Border.all(color: TppColors.line),
          ),
          child: Icon(icon, size: 18, color: TppColors.textSecondary),
        ),
      ),
    );
  }

  Widget _dropdown({
    required String label,
    required String Function() getValue,
    required List<String> items,
    required ValueChanged<String> onChanged,
  }) {
    return ValueListenableBuilder<int>(
      valueListenable: _rev,
      builder: (context, _, _) {
        final value = getValue();
        return InputDecorator(
          decoration: InputDecoration(labelText: label),
          child: DropdownButtonHideUnderline(
            child: DropdownButton<String>(
              value: items.contains(value) ? value : items.first,
              isExpanded: true,
              dropdownColor: TppColors.bgElevated,
              style: const TextStyle(color: TppColors.textPrimary, fontSize: 14),
              items: items
                  .map((e) => DropdownMenuItem(value: e, child: Text(e)))
                  .toList(),
              onChanged: (v) {
                if (v != null) onChanged(v);
              },
            ),
          ),
        );
      },
    );
  }
}

class CalcPreviewPanel extends StatelessWidget {
  const CalcPreviewPanel({super.key, required this.computed});

  final PhoneComputed computed;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(14),
      decoration: BoxDecoration(
        color: TppColors.bgElevated,
        borderRadius: BorderRadius.circular(14),
        border: Border.all(color: TppColors.line),
      ),
      child: Column(
        children: [
          _row('Total Money', formatMoneyOrDash(computed.totalMoney, computed.sold)),
          _row('Total Profit', formatMoneyOrDash(computed.totalProfit, computed.sold)),
          _row("Asif's Profit", formatMoneyOrDash(computed.asifProfit, computed.sold)),
          _row("Shozab's Profit", formatMoneyOrDash(computed.shozabProfit, computed.sold),
              last: true),
        ],
      ),
    );
  }

  Widget _row(String label, String value, {bool last = false}) {
    return Padding(
      padding: EdgeInsets.only(bottom: last ? 0 : 10),
      child: Row(
        children: [
          Expanded(
            child: Text(
              label,
              style: const TextStyle(color: TppColors.textSecondary, fontSize: 13),
            ),
          ),
          Text(
            value,
            style: const TextStyle(
              color: TppColors.accent,
              fontWeight: FontWeight.w700,
            ),
          ),
        ],
      ),
    );
  }
}
