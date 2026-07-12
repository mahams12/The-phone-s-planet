import 'dart:async';

import 'package:flutter/material.dart';

import '../data/phone.dart';
import '../state/phone_controller.dart';
import '../theme/colors.dart';
import '../widgets/common.dart';

class InventoryScreen extends StatefulWidget {
  const InventoryScreen({
    super.key,
    required this.controller,
    required this.onDelete,
  });

  final PhoneController controller;
  final ValueChanged<Phone> onDelete;

  @override
  State<InventoryScreen> createState() => _InventoryScreenState();
}

class _InventoryScreenState extends State<InventoryScreen> {
  late final TextEditingController _searchController;
  Timer? _searchDebounce;

  @override
  void initState() {
    super.initState();
    _searchController = TextEditingController(text: widget.controller.search);
  }

  @override
  void dispose() {
    _searchDebounce?.cancel();
    _searchController.dispose();
    super.dispose();
  }

  void _onSearchChanged(String query) {
    _searchDebounce?.cancel();
    _searchDebounce = Timer(const Duration(milliseconds: 200), () {
      widget.controller.setSearch(query);
    });
  }

  @override
  Widget build(BuildContext context) {
    final controller = widget.controller;
    final phones = controller.filteredPhones;
    final width = MediaQuery.sizeOf(context).width;
    final columns = width > 1100
        ? 4
        : width > 840
            ? 3
            : width > 560
                ? 2
                : 1;

    return Column(
      crossAxisAlignment: CrossAxisAlignment.stretch,
      children: [
        TextField(
          controller: _searchController,
          onChanged: _onSearchChanged,
          decoration: const InputDecoration(
            hintText: 'Search model, IMEI, colour…',
            prefixIcon: Icon(Icons.search, color: TppColors.muted),
          ),
        ),
        const SizedBox(height: 12),
        SingleChildScrollView(
          scrollDirection: Axis.horizontal,
          child: Row(
            children: [
              FilterChipButton(
                label: 'All Brands',
                selected: controller.companyFilter == 'all',
                onTap: () => controller.setCompanyFilter('all'),
              ),
              const SizedBox(width: 8),
              ...controller.companies.map(
                (c) => Padding(
                  padding: const EdgeInsets.only(right: 8),
                  child: FilterChipButton(
                    label: c,
                    selected: controller.companyFilter == c,
                    onTap: () => controller.setCompanyFilter(c),
                  ),
                ),
              ),
            ],
          ),
        ),
        const SizedBox(height: 10),
        Row(
          children: [
            FilterChipButton(
              label: 'All',
              selected: controller.statusFilter == StatusFilter.all,
              onTap: () => controller.setStatusFilter(StatusFilter.all),
            ),
            const SizedBox(width: 8),
            FilterChipButton(
              label: 'In Stock',
              selected: controller.statusFilter == StatusFilter.stock,
              onTap: () => controller.setStatusFilter(StatusFilter.stock),
            ),
            const SizedBox(width: 8),
            FilterChipButton(
              label: 'Sold',
              selected: controller.statusFilter == StatusFilter.sold,
              onTap: () => controller.setStatusFilter(StatusFilter.sold),
            ),
          ],
        ),
        const SizedBox(height: 10),
        SingleChildScrollView(
          scrollDirection: Axis.horizontal,
          child: Row(
            children: [
              const Padding(
                padding: EdgeInsets.only(right: 8),
                child: Icon(Icons.battery_full,
                    size: 18, color: TppColors.muted),
              ),
              FilterChipButton(
                label: 'Any Battery',
                selected: controller.batteryFilter == BatteryFilter.all,
                onTap: () => controller.setBatteryFilter(BatteryFilter.all),
              ),
              const SizedBox(width: 8),
              FilterChipButton(
                label: '90%+',
                selected: controller.batteryFilter == BatteryFilter.high,
                onTap: () => controller.setBatteryFilter(BatteryFilter.high),
              ),
              const SizedBox(width: 8),
              FilterChipButton(
                label: '80–89%',
                selected: controller.batteryFilter == BatteryFilter.mid,
                onTap: () => controller.setBatteryFilter(BatteryFilter.mid),
              ),
              const SizedBox(width: 8),
              FilterChipButton(
                label: 'Below 80%',
                selected: controller.batteryFilter == BatteryFilter.low,
                onTap: () => controller.setBatteryFilter(BatteryFilter.low),
              ),
            ],
          ),
        ),
        const SizedBox(height: 16),
        Expanded(
          child: phones.isEmpty
              ? Center(
                  child: Column(
                    mainAxisSize: MainAxisSize.min,
                    children: [
                      Icon(
                        Icons.public,
                        size: 48,
                        color: TppColors.accent.withValues(alpha: 0.5),
                      ),
                      const SizedBox(height: 12),
                      Text(
                        controller.phones.isEmpty
                            ? 'No phones yet'
                            : 'Nothing matches',
                        style: const TextStyle(
                          color: TppColors.textSecondary,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ],
                  ),
                )
              : GridView.builder(
                  gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
                    crossAxisCount: columns,
                    crossAxisSpacing: 12,
                    mainAxisSpacing: 12,
                    mainAxisExtent: 184,
                  ),
                  itemCount: phones.length,
                  itemBuilder: (context, index) {
                    final phone = phones[index];
                    return PhoneCard(
                      phone: phone,
                      highlighted: controller.highlightId == phone.id,
                      onTap: () => controller.openPreview(phone),
                      onEdit: () => controller.openEditForm(phone),
                      onDelete: () => widget.onDelete(phone),
                    );
                  },
                ),
        ),
      ],
    );
  }
}
