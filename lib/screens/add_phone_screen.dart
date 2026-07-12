import 'package:flutter/material.dart';

import '../data/phone.dart';
import '../theme/colors.dart';
import '../widgets/common.dart';
import '../widgets/phone_form_fields.dart';

class AddPhoneScreen extends StatefulWidget {
  const AddPhoneScreen({
    super.key,
    required this.editingPhone,
    required this.onSave,
    required this.onClear,
    required this.onCancelEdit,
  });

  final Phone? editingPhone;
  final ValueChanged<Phone> onSave;
  final VoidCallback onClear;
  final VoidCallback onCancelEdit;

  @override
  State<AddPhoneScreen> createState() => _AddPhoneScreenState();
}

class _AddPhoneScreenState extends State<AddPhoneScreen> {
  late PhoneFormData _form;
  Phone? _boundPhone;
  int _formEpoch = 0;

  @override
  void initState() {
    super.initState();
    _syncForm();
  }

  @override
  void didUpdateWidget(covariant AddPhoneScreen oldWidget) {
    super.didUpdateWidget(oldWidget);
    if (widget.editingPhone?.id != _boundPhone?.id) {
      _syncForm();
    }
  }

  void _syncForm() {
    _boundPhone = widget.editingPhone;
    _form = widget.editingPhone != null
        ? PhoneFormData.fromPhone(widget.editingPhone!)
        : (PhoneFormData()
          ..datePurchased = DateTime.now().millisecondsSinceEpoch);
  }

  @override
  Widget build(BuildContext context) {
    final isEdit = widget.editingPhone != null;

    return ListView(
      padding: const EdgeInsets.only(bottom: 32),
      children: [
        Text(
          isEdit ? 'Edit Phone' : 'Add Phone',
          style: const TextStyle(
            fontSize: 24,
            fontWeight: FontWeight.w800,
            color: TppColors.textPrimary,
          ),
        ),
        if (isEdit) ...[
          const SizedBox(height: 12),
          Container(
            padding: const EdgeInsets.all(12),
            decoration: BoxDecoration(
              color: TppColors.warnDim,
              borderRadius: BorderRadius.circular(12),
              border: Border.all(color: TppColors.warn.withValues(alpha: 0.4)),
            ),
            child: Row(
              children: [
                const Icon(Icons.edit_note, color: TppColors.warn),
                const SizedBox(width: 8),
                const Expanded(
                  child: Text(
                    'Editing existing phone',
                    style: TextStyle(
                      color: TppColors.warn,
                      fontWeight: FontWeight.w600,
                    ),
                  ),
                ),
                TextButton(
                  onPressed: () {
                    widget.onCancelEdit();
                    setState(_syncForm);
                  },
                  child: const Text('Cancel edit'),
                ),
              ],
            ),
          ),
        ],
        const SizedBox(height: 18),
        PhoneFormFields(
          key: ValueKey('${_boundPhone?.id ?? 'new'}-$_formEpoch'),
          data: _form,
        ),
        const SizedBox(height: 20),
        Row(
          children: [
            Expanded(
              child: TppGhostButton(
                label: 'Clear',
                onPressed: () {
                  widget.onClear();
                  setState(() {
                    _form = PhoneFormData();
                    _boundPhone = null;
                    _formEpoch++;
                  });
                },
              ),
            ),
            const SizedBox(width: 12),
            Expanded(
              flex: 2,
              child: TppPrimaryButton(
                label: isEdit ? 'Update' : 'Save',
                onPressed: () {
                  if (_form.model.trim().isEmpty) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(content: Text('Model is required')),
                    );
                    return;
                  }
                  if (_form.sold && _form.dateSold <= 0) {
                    ScaffoldMessenger.of(context).showSnackBar(
                      const SnackBar(
                        content: Text('Date Sold is required for sold phones'),
                      ),
                    );
                    return;
                  }
                  widget.onSave(_form.toPhone());
                },
              ),
            ),
          ],
        ),
      ],
    );
  }
}
