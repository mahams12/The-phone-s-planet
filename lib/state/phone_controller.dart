import 'dart:async';

import 'package:flutter/foundation.dart';
import 'package:uuid/uuid.dart';

import '../data/phone.dart';
import '../data/phone_repository.dart';

enum AppScreen { dashboard, budget, add, inventory }

enum StatusFilter { all, stock, sold }

enum BatteryFilter { all, high, mid, low }

class PhoneController extends ChangeNotifier {
  PhoneController({PhoneRepository? repository})
      : _repository = repository ?? PhoneRepository() {
    _subscription = _repository.observePhones().listen(
      (list) {
        _setPhones(list);
        isLoading = false;
        notifyListeners();
      },
      onError: (_) {
        _setPhones(const []);
        isLoading = false;
        notifyListeners();
      },
    );
  }

  final PhoneRepository _repository;
  StreamSubscription<List<Phone>>? _subscription;
  final _uuid = const Uuid();

  List<Phone> phones = [];

  // Cached derived data, recomputed only when the phones list changes.
  PhoneTotals _totals = const PhoneTotals();
  List<String> _companies = const [];

  void _setPhones(List<Phone> list) {
    phones = list;
    _totals = computeAll(list);
    _companies = list
        .map((p) => p.company)
        .where((c) => c.trim().isNotEmpty)
        .toSet()
        .toList()
      ..sort();
  }
  String search = '';
  String companyFilter = 'all';
  StatusFilter statusFilter = StatusFilter.all;
  BatteryFilter batteryFilter = BatteryFilter.all;
  bool isLoading = true;
  AppScreen currentScreen = AppScreen.dashboard;
  Phone? editingPhone;
  Phone? previewPhone;
  String? highlightId;
  String? toastMessage;
  String? errorMessage;

  List<Phone> get filteredPhones =>
      _filterPhones(phones, search, companyFilter, statusFilter, batteryFilter);

  PhoneTotals get totals => _totals;

  List<String> get companies => _companies;

  void navigateTo(AppScreen screen, {bool keepForm = false}) {
    if (screen == AppScreen.add && !keepForm) {
      editingPhone = null;
    }
    currentScreen = screen;
    notifyListeners();
  }

  void setSearch(String query) {
    search = query;
    notifyListeners();
  }

  void setCompanyFilter(String company) {
    companyFilter = company;
    notifyListeners();
  }

  void setStatusFilter(StatusFilter filter) {
    statusFilter = filter;
    notifyListeners();
  }

  void setBatteryFilter(BatteryFilter filter) {
    batteryFilter = filter;
    notifyListeners();
  }

  void openEditForm(Phone phone) {
    editingPhone = phone;
    previewPhone = null;
    currentScreen = AppScreen.add;
    notifyListeners();
  }

  void resetForm() {
    editingPhone = null;
    notifyListeners();
  }

  void openPreview(Phone phone) {
    previewPhone = phone;
    notifyListeners();
  }

  void closePreview() {
    previewPhone = null;
    notifyListeners();
  }

  void clearHighlight() {
    highlightId = null;
    notifyListeners();
  }

  void clearToast() {
    toastMessage = null;
    notifyListeners();
  }

  void clearError() {
    errorMessage = null;
    notifyListeners();
  }

  Future<void> savePhone(Phone phone) async {
    try {
      final isEdit =
          phone.id.isNotEmpty && phones.any((p) => p.id == phone.id);
      final id = phone.id.isEmpty ? _uuid.v4() : phone.id;
      final existing = phones.cast<Phone?>().firstWhere(
            (p) => p?.id == id,
            orElse: () => null,
          );
      final toSave = phone.copyWith(
        id: id,
        createdAt: existing?.createdAt ?? DateTime.now().millisecondsSinceEpoch,
      );
      await _repository.savePhone(toSave);
      editingPhone = null;
      highlightId = id;
      toastMessage = isEdit ? 'Phone updated' : 'Phone added to inventory';
      currentScreen = AppScreen.inventory;
      notifyListeners();

      Future<void>.delayed(const Duration(milliseconds: 1500), () {
        if (highlightId == id) {
          highlightId = null;
          notifyListeners();
        }
      });
    } catch (e) {
      errorMessage = 'Failed to save: $e';
      notifyListeners();
    }
  }

  Future<void> deletePhone(Phone phone) async {
    try {
      await _repository.deletePhone(phone.id);
      previewPhone = null;
      toastMessage = 'Phone deleted';
      notifyListeners();
    } catch (e) {
      errorMessage = 'Failed to delete: $e';
      notifyListeners();
    }
  }

  List<Phone> _filterPhones(
    List<Phone> list,
    String searchQuery,
    String company,
    StatusFilter status,
    BatteryFilter battery,
  ) {
    final s = searchQuery.trim().toLowerCase();
    return list.where((phone) {
      if (company != 'all' && phone.company != company) return false;
      final c = computePhone(phone);
      switch (status) {
        case StatusFilter.sold:
          if (!c.sold) return false;
        case StatusFilter.stock:
          if (c.sold) return false;
        case StatusFilter.all:
          break;
      }
      switch (battery) {
        case BatteryFilter.high:
          if (phone.batteryHealth < 90) return false;
        case BatteryFilter.mid:
          if (phone.batteryHealth < 80 || phone.batteryHealth >= 90) {
            return false;
          }
        case BatteryFilter.low:
          if (phone.batteryHealth <= 0 || phone.batteryHealth >= 80) {
            return false;
          }
        case BatteryFilter.all:
          break;
      }
      if (s.isNotEmpty) {
        final hay = [
          phone.company,
          phone.model,
          phone.detail,
          phone.imei1,
          phone.imei2,
          phone.colour,
          phone.storage,
          phone.batteryHealth > 0 ? '${phone.batteryHealth}%' : '',
        ].join(' ').toLowerCase();
        if (!hay.contains(s)) return false;
      }
      return true;
    }).toList();
  }

  @override
  void dispose() {
    _subscription?.cancel();
    super.dispose();
  }
}
