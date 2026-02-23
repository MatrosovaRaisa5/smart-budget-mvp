import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/budget_provider.dart';

class SetupScreen extends StatefulWidget {
  @override
  State<SetupScreen> createState() => _SetupScreenState();
}

class _SetupScreenState extends State<SetupScreen> {
  final _formKey = GlobalKey<FormState>();
  final _incomeController = TextEditingController();

  // Стандартные проценты
  Map<String, double> _percentages = {
    'GROCERIES': 25,
    'UTILITIES': 15,
    'ENTERTAINMENT': 10,
    'TRANSPORT': 10,
    'SAVINGS': 20,
    'OTHER': 20,
  };

  final List<String> _categoryNames = [
    'GROCERIES', 'UTILITIES', 'ENTERTAINMENT', 'TRANSPORT', 'SAVINGS', 'OTHER'
  ];

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(title: Text('Настройка бюджета')),
      body: Form(
        key: _formKey,
        child: ListView(
          padding: EdgeInsets.all(16),
          children: [
            TextFormField(
              controller: _incomeController,
              decoration: InputDecoration(labelText: 'Месячный доход (₽)'),
              keyboardType: TextInputType.number,
              validator: (value) {
                if (value == null || value.isEmpty) return 'Введите доход';
                if (double.tryParse(value) == null) return 'Введите число';
                return null;
              },
            ),
            SizedBox(height: 20),
            Text('Распределение по категориям (сумма должна быть 100%)',
                style: TextStyle(fontWeight: FontWeight.bold)),
            ..._categoryNames.map((catName) {
              return Padding(
                padding: const EdgeInsets.symmetric(vertical: 8.0),
                child: Row(
                  children: [
                    Expanded(flex: 2, child: Text(_getCategoryName(catName))),
                    Expanded(
                      flex: 1,
                      child: TextFormField(
                        initialValue: _percentages[catName].toString(),
                        decoration: InputDecoration(labelText: '%'),
                        keyboardType: TextInputType.number,
                        onChanged: (value) {
                          setState(() {
                            _percentages[catName] = double.tryParse(value) ?? 0;
                          });
                        },
                      ),
                    ),
                  ],
                ),
              );
            }).toList(),
            SizedBox(height: 20),
            Consumer<BudgetProvider>(
              builder: (ctx, provider, child) {
                return ElevatedButton(
                  onPressed: () async {
                    if (_formKey.currentState!.validate()) {
                      // Проверка суммы процентов
                      double total = _percentages.values.fold(0, (a, b) => a + b);
                      if ((total - 100).abs() > 0.1) {
                        ScaffoldMessenger.of(context).showSnackBar(
                          SnackBar(content: Text('Сумма процентов должна быть 100% (сейчас $total%)')),
                        );
                        return;
                      }

                      // Преобразуем проценты в доли (25% -> 0.25)
                      Map<String, double> fractions = {};
                      _percentages.forEach((key, value) {
                        fractions[key] = value / 100;
                      });

                      await provider.setupBudget(
                        double.parse(_incomeController.text),
                        fractions,
                      );

                      if (context.mounted) {
                        Navigator.pop(context);
                      }
                    }
                  },
                  child: Text('Сохранить'),
                );
              },
            ),
          ],
        ),
      ),
    );
  }

  String _getCategoryName(String cat) {
    switch (cat) {
      case 'GROCERIES': return 'Продукты';
      case 'UTILITIES': return 'Коммунальные';
      case 'ENTERTAINMENT': return 'Развлечения';
      case 'TRANSPORT': return 'Транспорт';
      case 'SAVINGS': return 'Накопления';
      default: return 'Остальное';
    }
  }
}