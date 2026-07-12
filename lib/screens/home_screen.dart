import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

import '../data/phone.dart';
import '../state/phone_controller.dart';
import '../theme/colors.dart';
import 'add_phone_screen.dart';
import 'budget_screen.dart';
import 'dashboard_screen.dart';
import 'inventory_screen.dart';
import 'phone_preview_sheet.dart';

class HomeScreen extends StatefulWidget {
  const HomeScreen({super.key});

  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  String? _shownToast;
  String? _shownError;
  String? _shownPreviewId;
  final _scaffoldKey = GlobalKey<ScaffoldState>();

  void _select(PhoneController controller, AppScreen screen) {
    final keepForm =
        screen == AppScreen.add && controller.editingPhone != null;
    controller.navigateTo(screen, keepForm: keepForm);
  }

  @override
  Widget build(BuildContext context) {
    final controller = context.watch<PhoneController>();

    WidgetsBinding.instance.addPostFrameCallback((_) {
      _handleSideEffects(controller);
    });

    return Scaffold(
      key: _scaffoldKey,
      drawer: _SideBar(
        current: controller.currentScreen,
        onSelect: (screen) {
          Navigator.of(context).pop();
          _select(controller, screen);
        },
      ),
      body: Container(
        decoration: const BoxDecoration(
          color: TppColors.bg,
          gradient: RadialGradient(
            center: Alignment(-0.7, -1),
            radius: 1.2,
            colors: [Color(0x145EEAD4), Colors.transparent],
          ),
        ),
        child: Material(
          type: MaterialType.transparency,
          child: SafeArea(
            child: Center(
              child: ConstrainedBox(
                constraints: const BoxConstraints(maxWidth: 1280),
                child: Padding(
                  padding: const EdgeInsets.symmetric(horizontal: 16),
                  child: Column(
                    children: [
                      const SizedBox(height: 8),
                      _TopBar(
                        current: controller.currentScreen,
                        onMenu: () => _scaffoldKey.currentState?.openDrawer(),
                        onSelect: (screen) => _select(controller, screen),
                      ),
                      const SizedBox(height: 16),
                      Expanded(
                        child: AnimatedSwitcher(
                          duration: const Duration(milliseconds: 250),
                          child: KeyedSubtree(
                            key: ValueKey(controller.currentScreen),
                            child: _body(controller),
                          ),
                        ),
                      ),
                    ],
                  ),
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }

  Widget _body(PhoneController controller) {
    if (controller.isLoading) {
      return const Center(
        child: CircularProgressIndicator(color: TppColors.accent),
      );
    }

    switch (controller.currentScreen) {
      case AppScreen.dashboard:
        return DashboardScreen(
          phones: controller.phones,
          totals: controller.totals,
          onOpenPhone: controller.openPreview,
        );
      case AppScreen.budget:
        return BudgetScreen(phones: controller.phones);
      case AppScreen.add:
        return AddPhoneScreen(
          editingPhone: controller.editingPhone,
          onSave: controller.savePhone,
          onClear: controller.resetForm,
          onCancelEdit: controller.resetForm,
        );
      case AppScreen.inventory:
        return InventoryScreen(
          controller: controller,
          onDelete: (phone) => _confirmDelete(controller, phone),
        );
    }
  }

  void _handleSideEffects(PhoneController controller) {
    final toast = controller.toastMessage;
    if (toast != null && toast != _shownToast) {
      _shownToast = toast;
      ScaffoldMessenger.of(context).showSnackBar(
        SnackBar(
          content: Text(toast),
          backgroundColor: TppColors.bgElevated,
        ),
      );
      controller.clearToast();
    }

    final error = controller.errorMessage;
    if (error != null && error != _shownError) {
      _shownError = error;
      showDialog<void>(
        context: context,
        builder: (context) => AlertDialog(
          backgroundColor: TppColors.bgPanel,
          title: const Text('Error'),
          content: Text(error),
          actions: [
            TextButton(
              onPressed: () {
                Navigator.pop(context);
                controller.clearError();
              },
              child: const Text('OK'),
            ),
          ],
        ),
      );
    }

    final preview = controller.previewPhone;
    if (preview != null && preview.id != _shownPreviewId) {
      _shownPreviewId = preview.id;
      showPhonePreviewSheet(
        context: context,
        phone: preview,
        onEdit: () => controller.openEditForm(preview),
        onDelete: () => _confirmDelete(controller, preview),
      ).whenComplete(() {
        if (controller.previewPhone?.id == preview.id) {
          controller.closePreview();
        }
        _shownPreviewId = null;
      });
    }
  }

  Future<void> _confirmDelete(PhoneController controller, Phone phone) async {
    final confirmed = await showDialog<bool>(
      context: context,
      builder: (context) => AlertDialog(
        backgroundColor: TppColors.bgPanel,
        title: const Text('Delete phone?'),
        content: Text('Remove ${phone.model} from inventory?'),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(context, false),
            child: const Text('Cancel'),
          ),
          TextButton(
            onPressed: () => Navigator.pop(context, true),
            style: TextButton.styleFrom(foregroundColor: TppColors.danger),
            child: const Text('Delete'),
          ),
        ],
      ),
    );
    if (confirmed == true) {
      await controller.deletePhone(phone);
    }
  }
}

class _TopBar extends StatelessWidget {
  const _TopBar({
    required this.current,
    required this.onSelect,
    required this.onMenu,
  });

  final AppScreen current;
  final ValueChanged<AppScreen> onSelect;
  final VoidCallback onMenu;

  @override
  Widget build(BuildContext context) {
    final isNarrow = MediaQuery.sizeOf(context).width < 520;

    final menuButton = _MenuButton(onTap: onMenu);

    final brand = Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        menuButton,
        const SizedBox(width: 8),
        Container(
          width: 36,
          height: 36,
          decoration: BoxDecoration(
            shape: BoxShape.circle,
            gradient: const LinearGradient(
              colors: [TppColors.accentGradientStart, TppColors.accentGradientEnd],
            ),
            boxShadow: [
              BoxShadow(
                color: TppColors.glowRing.withValues(alpha: 0.3),
                blurRadius: 12,
              ),
            ],
          ),
          child: const Icon(Icons.public, color: TppColors.accentOn, size: 20),
        ),
        const SizedBox(width: 10),
        const Text(
          'TPP',
          style: TextStyle(
            fontSize: 22,
            fontWeight: FontWeight.w800,
            letterSpacing: 0.5,
          ),
        ),
      ],
    );

    if (isNarrow) {
      return Column(
        crossAxisAlignment: CrossAxisAlignment.stretch,
        children: [
          Align(alignment: Alignment.centerLeft, child: brand),
          const SizedBox(height: 12),
          _TabBar(current: current, onSelect: onSelect, expanded: true),
        ],
      );
    }

    return Row(
      children: [
        brand,
        const Spacer(),
        Flexible(
          child: _TabBar(current: current, onSelect: onSelect),
        ),
      ],
    );
  }
}

class _TabBar extends StatelessWidget {
  const _TabBar({
    required this.current,
    required this.onSelect,
    this.expanded = false,
  });

  final AppScreen current;
  final ValueChanged<AppScreen> onSelect;
  final bool expanded;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(4),
      decoration: BoxDecoration(
        color: TppColors.bgElevated,
        borderRadius: BorderRadius.circular(999),
        border: Border.all(color: TppColors.line),
      ),
      child: Row(
        mainAxisSize: expanded ? MainAxisSize.max : MainAxisSize.min,
        children: [
          _tab('Dashboard', AppScreen.dashboard),
          _tab('Add', AppScreen.add),
          _tab('Inventory', AppScreen.inventory),
        ],
      ),
    );
  }

  Widget _tab(String label, AppScreen screen) {
    final selected = current == screen;
    final tab = GestureDetector(
      onTap: () => onSelect(screen),
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 180),
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
        decoration: BoxDecoration(
          color: selected ? TppColors.accentDim : Colors.transparent,
          borderRadius: BorderRadius.circular(999),
        ),
        child: Text(
          label,
          textAlign: TextAlign.center,
          maxLines: 1,
          overflow: TextOverflow.ellipsis,
          style: TextStyle(
            color: selected ? TppColors.accent : TppColors.textSecondary,
            fontWeight: FontWeight.w700,
            fontSize: 13,
          ),
        ),
      ),
    );
    return expanded ? Expanded(child: tab) : Flexible(child: tab);
  }
}

class _MenuButton extends StatelessWidget {
  const _MenuButton({required this.onTap});

  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Material(
      color: TppColors.bgElevated,
      borderRadius: BorderRadius.circular(12),
      child: InkWell(
        onTap: onTap,
        borderRadius: BorderRadius.circular(12),
        child: Container(
          width: 40,
          height: 40,
          alignment: Alignment.center,
          decoration: BoxDecoration(
            borderRadius: BorderRadius.circular(12),
            border: Border.all(color: TppColors.line),
          ),
          child: const Icon(Icons.menu, size: 20, color: TppColors.textSecondary),
        ),
      ),
    );
  }
}

class _SideBar extends StatelessWidget {
  const _SideBar({required this.current, required this.onSelect});

  final AppScreen current;
  final ValueChanged<AppScreen> onSelect;

  static const _items = [
    (AppScreen.dashboard, 'Dashboard', Icons.dashboard_outlined),
    (AppScreen.budget, 'Budget', Icons.account_balance_wallet_outlined),
    (AppScreen.add, 'Add Phone', Icons.add_circle_outline),
    (AppScreen.inventory, 'Inventory', Icons.inventory_2_outlined),
  ];

  @override
  Widget build(BuildContext context) {
    return Drawer(
      backgroundColor: TppColors.bgPanel,
      shape: const RoundedRectangleBorder(
        borderRadius: BorderRadius.horizontal(right: Radius.circular(20)),
      ),
      child: SafeArea(
        child: Column(
          crossAxisAlignment: CrossAxisAlignment.stretch,
          children: [
            Padding(
              padding: const EdgeInsets.fromLTRB(20, 24, 20, 20),
              child: Row(
                children: [
                  Container(
                    width: 42,
                    height: 42,
                    decoration: BoxDecoration(
                      shape: BoxShape.circle,
                      gradient: const LinearGradient(
                        colors: [
                          TppColors.accentGradientStart,
                          TppColors.accentGradientEnd,
                        ],
                      ),
                      boxShadow: [
                        BoxShadow(
                          color: TppColors.glowRing.withValues(alpha: 0.3),
                          blurRadius: 12,
                        ),
                      ],
                    ),
                    child: const Icon(Icons.public,
                        color: TppColors.accentOn, size: 22),
                  ),
                  const SizedBox(width: 12),
                  Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    mainAxisSize: MainAxisSize.min,
                    children: const [
                      Text(
                        'TPP',
                        style: TextStyle(
                          fontSize: 20,
                          fontWeight: FontWeight.w800,
                          letterSpacing: 0.5,
                        ),
                      ),
                      Text(
                        "The Phone's Planet",
                        style: TextStyle(color: TppColors.muted, fontSize: 12),
                      ),
                    ],
                  ),
                ],
              ),
            ),
            const Divider(color: TppColors.line, height: 1),
            const SizedBox(height: 8),
            for (final item in _items)
              _SideBarItem(
                label: item.$2,
                icon: item.$3,
                selected: current == item.$1,
                onTap: () => onSelect(item.$1),
              ),
            const Spacer(),
            const Padding(
              padding: EdgeInsets.all(20),
              child: Text(
                'Inventory & Budget',
                style: TextStyle(color: TppColors.muted2, fontSize: 11),
              ),
            ),
          ],
        ),
      ),
    );
  }
}

class _SideBarItem extends StatelessWidget {
  const _SideBarItem({
    required this.label,
    required this.icon,
    required this.selected,
    required this.onTap,
  });

  final String label;
  final IconData icon;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return Padding(
      padding: const EdgeInsets.symmetric(horizontal: 12, vertical: 3),
      child: Material(
        color: selected ? TppColors.accentDim : Colors.transparent,
        borderRadius: BorderRadius.circular(12),
        child: InkWell(
          onTap: onTap,
          borderRadius: BorderRadius.circular(12),
          child: Container(
            padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 12),
            decoration: BoxDecoration(
              borderRadius: BorderRadius.circular(12),
              border: Border.all(
                color: selected
                    ? TppColors.accent.withValues(alpha: 0.4)
                    : Colors.transparent,
              ),
            ),
            child: Row(
              children: [
                Icon(
                  icon,
                  size: 20,
                  color: selected ? TppColors.accent : TppColors.textSecondary,
                ),
                const SizedBox(width: 14),
                Text(
                  label,
                  style: TextStyle(
                    color:
                        selected ? TppColors.accent : TppColors.textSecondary,
                    fontWeight: FontWeight.w700,
                    fontSize: 15,
                  ),
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}
