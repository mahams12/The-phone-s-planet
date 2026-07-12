import 'package:flutter/material.dart';

import '../data/phone.dart';
import '../theme/colors.dart';
import '../util/formatters.dart';

class StatusPill extends StatelessWidget {
  const StatusPill({super.key, required this.sold});

  final bool sold;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.symmetric(horizontal: 10, vertical: 4),
      decoration: BoxDecoration(
        color: sold ? TppColors.accentDim : const Color(0x22333A45),
        borderRadius: BorderRadius.circular(999),
        border: Border.all(
          color: sold ? TppColors.accent.withValues(alpha: 0.35) : TppColors.line,
        ),
      ),
      child: Text(
        sold ? 'SOLD' : 'IN STOCK',
        style: TextStyle(
          fontSize: 11,
          fontWeight: FontWeight.w700,
          letterSpacing: 0.6,
          color: sold ? TppColors.accent : TppColors.muted,
        ),
      ),
    );
  }
}

class StatCard extends StatelessWidget {
  const StatCard({
    super.key,
    required this.label,
    required this.value,
    this.subtitle,
    this.accent = TppColors.accent,
  });

  final String label;
  final String value;
  final String? subtitle;
  final Color accent;

  @override
  Widget build(BuildContext context) {
    return Container(
      padding: const EdgeInsets.all(16),
      decoration: BoxDecoration(
        color: TppColors.bgPanel,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(color: TppColors.line),
        boxShadow: const [
          BoxShadow(
            color: Color(0x66000000),
            blurRadius: 18,
            offset: Offset(0, 10),
          ),
        ],
      ),
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisAlignment: MainAxisAlignment.spaceBetween,
        children: [
          Text(
            label,
            maxLines: 1,
            overflow: TextOverflow.ellipsis,
            style: const TextStyle(
              color: TppColors.textSecondary,
              fontSize: 13,
              fontWeight: FontWeight.w500,
            ),
          ),
          Flexible(
            child: FittedBox(
              fit: BoxFit.scaleDown,
              alignment: Alignment.centerLeft,
              child: Text(
                value,
                maxLines: 1,
                style: TextStyle(
                  color: accent,
                  fontSize: 22,
                  fontWeight: FontWeight.w700,
                ),
              ),
            ),
          ),
          if (subtitle != null)
            Text(
              subtitle!,
              maxLines: 1,
              overflow: TextOverflow.ellipsis,
              style: const TextStyle(color: TppColors.muted, fontSize: 12),
            ),
        ],
      ),
    );
  }
}

class FilterChipButton extends StatelessWidget {
  const FilterChipButton({
    super.key,
    required this.label,
    required this.selected,
    required this.onTap,
  });

  final String label;
  final bool selected;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return GestureDetector(
      onTap: onTap,
      child: AnimatedContainer(
        duration: const Duration(milliseconds: 180),
        padding: const EdgeInsets.symmetric(horizontal: 14, vertical: 8),
        decoration: BoxDecoration(
          color: selected ? TppColors.accentDim : TppColors.bgElevated,
          borderRadius: BorderRadius.circular(999),
          border: Border.all(
            color: selected ? TppColors.accent.withValues(alpha: 0.5) : TppColors.line,
          ),
        ),
        child: Text(
          label,
          style: TextStyle(
            color: selected ? TppColors.accent : TppColors.textSecondary,
            fontWeight: FontWeight.w600,
            fontSize: 13,
          ),
        ),
      ),
    );
  }
}

class TppPrimaryButton extends StatelessWidget {
  const TppPrimaryButton({
    super.key,
    required this.label,
    required this.onPressed,
  });

  final String label;
  final VoidCallback? onPressed;

  @override
  Widget build(BuildContext context) {
    return DecoratedBox(
      decoration: BoxDecoration(
        borderRadius: BorderRadius.circular(14),
        gradient: const LinearGradient(
          colors: [TppColors.accentGradientStart, TppColors.accentGradientEnd],
        ),
        boxShadow: [
          BoxShadow(
            color: TppColors.glowRing.withValues(alpha: 0.25),
            blurRadius: 16,
            offset: const Offset(0, 6),
          ),
        ],
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          onTap: onPressed,
          borderRadius: BorderRadius.circular(14),
          child: Padding(
            padding: const EdgeInsets.symmetric(horizontal: 18, vertical: 14),
            child: Center(
              child: Text(
                label,
                style: const TextStyle(
                  color: TppColors.accentOn,
                  fontWeight: FontWeight.w700,
                  fontSize: 15,
                ),
              ),
            ),
          ),
        ),
      ),
    );
  }
}

class TppGhostButton extends StatelessWidget {
  const TppGhostButton({
    super.key,
    required this.label,
    required this.onPressed,
  });

  final String label;
  final VoidCallback? onPressed;

  @override
  Widget build(BuildContext context) {
    return OutlinedButton(
      onPressed: onPressed,
      style: OutlinedButton.styleFrom(
        foregroundColor: TppColors.textSecondary,
        side: const BorderSide(color: TppColors.line),
        padding: const EdgeInsets.symmetric(horizontal: 18, vertical: 14),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
      ),
      child: Text(label, style: const TextStyle(fontWeight: FontWeight.w600)),
    );
  }
}

class TppDangerButton extends StatelessWidget {
  const TppDangerButton({
    super.key,
    required this.label,
    required this.onPressed,
  });

  final String label;
  final VoidCallback? onPressed;

  @override
  Widget build(BuildContext context) {
    return OutlinedButton(
      onPressed: onPressed,
      style: OutlinedButton.styleFrom(
        foregroundColor: TppColors.danger,
        side: BorderSide(color: TppColors.danger.withValues(alpha: 0.5)),
        backgroundColor: TppColors.dangerDim,
        padding: const EdgeInsets.symmetric(horizontal: 18, vertical: 14),
        shape: RoundedRectangleBorder(borderRadius: BorderRadius.circular(14)),
      ),
      child: Text(label, style: const TextStyle(fontWeight: FontWeight.w700)),
    );
  }
}

class PhoneCard extends StatelessWidget {
  const PhoneCard({
    super.key,
    required this.phone,
    required this.highlighted,
    required this.onTap,
    required this.onEdit,
    required this.onDelete,
  });

  final Phone phone;
  final bool highlighted;
  final VoidCallback onTap;
  final VoidCallback onEdit;
  final VoidCallback onDelete;

  @override
  Widget build(BuildContext context) {
    final computed = computePhone(phone);
    final color = brandColor(phone.company);

    return AnimatedContainer(
      duration: const Duration(milliseconds: 300),
      decoration: BoxDecoration(
        color: TppColors.bgPanel,
        borderRadius: BorderRadius.circular(16),
        border: Border.all(
          color: highlighted
              ? TppColors.accent.withValues(alpha: 0.7)
              : TppColors.line,
          width: highlighted ? 1.5 : 1,
        ),
        boxShadow: [
          if (highlighted)
            BoxShadow(
              color: TppColors.accent.withValues(alpha: 0.25),
              blurRadius: 20,
            ),
          const BoxShadow(
            color: Color(0x55000000),
            blurRadius: 16,
            offset: Offset(0, 8),
          ),
        ],
      ),
      child: Material(
        color: Colors.transparent,
        child: InkWell(
          onTap: onTap,
          borderRadius: BorderRadius.circular(16),
          child: Padding(
            padding: const EdgeInsets.all(14),
            child: Column(
              crossAxisAlignment: CrossAxisAlignment.start,
              children: [
                Row(
                  children: [
                    Container(
                      width: 10,
                      height: 10,
                      decoration: BoxDecoration(
                        color: color,
                        shape: BoxShape.circle,
                        boxShadow: [
                          BoxShadow(
                            color: color.withValues(alpha: 0.5),
                            blurRadius: 8,
                          ),
                        ],
                      ),
                    ),
                    const SizedBox(width: 8),
                    Expanded(
                      child: Text(
                        phone.company.isEmpty ? 'Other' : phone.company,
                        style: const TextStyle(
                          color: TppColors.textSecondary,
                          fontSize: 12,
                          fontWeight: FontWeight.w600,
                        ),
                      ),
                    ),
                    StatusPill(sold: phone.sold),
                  ],
                ),
                const SizedBox(height: 10),
                Text(
                  phone.model,
                  maxLines: 2,
                  overflow: TextOverflow.ellipsis,
                  style: const TextStyle(
                    fontSize: 16,
                    fontWeight: FontWeight.w700,
                    color: TppColors.textPrimary,
                  ),
                ),
                const SizedBox(height: 4),
                Text(
                  [
                    if (phone.storage.isNotEmpty) phone.storage,
                    if (phone.colour.isNotEmpty) phone.colour,
                    if (phone.batteryHealth > 0) '${phone.batteryHealth}% batt',
                  ].join(' · '),
                  maxLines: 1,
                  overflow: TextOverflow.ellipsis,
                  style: const TextStyle(color: TppColors.muted, fontSize: 12),
                ),
                const Spacer(),
                Row(
                  children: [
                    Expanded(
                      child: Text(
                        formatMoneyOrDash(computed.totalProfit, computed.sold),
                        maxLines: 1,
                        overflow: TextOverflow.ellipsis,
                        style: TextStyle(
                          color: computed.sold
                              ? TppColors.accent
                              : TppColors.muted,
                          fontWeight: FontWeight.w700,
                        ),
                      ),
                    ),
                    _CardIconButton(
                      icon: Icons.edit_outlined,
                      color: TppColors.textSecondary,
                      onTap: onEdit,
                    ),
                    const SizedBox(width: 4),
                    _CardIconButton(
                      icon: Icons.delete_outline,
                      color: TppColors.danger,
                      onTap: onDelete,
                    ),
                  ],
                ),
              ],
            ),
          ),
        ),
      ),
    );
  }
}

class _CardIconButton extends StatelessWidget {
  const _CardIconButton({
    required this.icon,
    required this.color,
    required this.onTap,
  });

  final IconData icon;
  final Color color;
  final VoidCallback onTap;

  @override
  Widget build(BuildContext context) {
    return InkResponse(
      onTap: onTap,
      radius: 22,
      child: Padding(
        padding: const EdgeInsets.all(6),
        child: Icon(icon, size: 18, color: color),
      ),
    );
  }
}
