import 'package:intl/intl.dart';

final _moneyFormat = NumberFormat.decimalPattern('en_PK')
  ..maximumFractionDigits = 0;

final _dateFormat = DateFormat('d MMM yyyy');
final _monthFormat = DateFormat('MMMM yyyy');
final _monthShortFormat = DateFormat('MMM yyyy');

String formatMoney(double amount) => 'Rs ${_moneyFormat.format(amount)}';

String formatMoneyOrDash(double amount, bool sold) =>
    sold ? formatMoney(amount) : '—';

/// Formats an epoch-millis date, or a dash when unset (0).
String formatDate(int millis) {
  if (millis <= 0) return '—';
  return _dateFormat.format(DateTime.fromMillisecondsSinceEpoch(millis));
}

String formatMonth(DateTime month) => _monthFormat.format(month);

String formatMonthShort(DateTime month) => _monthShortFormat.format(month);
