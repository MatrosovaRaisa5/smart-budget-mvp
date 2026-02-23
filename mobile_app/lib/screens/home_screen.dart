import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../providers/budget_provider.dart';

class HomeScreen extends StatefulWidget {
  @override
  State<HomeScreen> createState() => _HomeScreenState();
}

class _HomeScreenState extends State<HomeScreen> {
  @override
  void initState() {
    super.initState();
    // Загружаем данные при старте
    Future.microtask(() => context.read<BudgetProvider>().loadStatus());
  }

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: AppBar(
        title: Text('Мой бюджет'),
        actions: [
          IconButton(
            icon: Icon(Icons.settings),
            onPressed: () => Navigator.pushNamed(context, '/setup'),
          ),
          IconButton(
            icon: Icon(Icons.refresh),
            onPressed: () => context.read<BudgetProvider>().loadStatus(),
          ),
        ],
      ),
      body: Consumer<BudgetProvider>(
        builder: (ctx, provider, child) {
          if (provider.isLoading) {
            return Center(child: CircularProgressIndicator());
          }

          // Если бюджет еще не настроен
          if (provider.income == 0 || provider.categories.isEmpty) {
            return Center(
              child: Column(
                mainAxisAlignment: MainAxisAlignment.center,
                children: [
                  Text('Бюджет еще не настроен'),
                  SizedBox(height: 20),
                  ElevatedButton(
                    onPressed: () => Navigator.pushNamed(context, '/setup'),
                    child: Text('Настроить бюджет'),
                  ),
                ],
              ),
            );
          }

          // Основной экран с бюджетом
          return Column(
            children: [
              // Карточка с алертами
              if (provider.alerts.isNotEmpty)
                Container(
                  margin: EdgeInsets.all(8),
                  padding: EdgeInsets.all(12),
                  color: Colors.amber.shade100,
                  child: Column(
                    crossAxisAlignment: CrossAxisAlignment.start,
                    children: provider.alerts.map((alert) => Text(alert)).toList(),
                  ),
                ),

              // Заголовок с доходом
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: Text(
                  'Доход: ${provider.income.toStringAsFixed(0)} ₽',
                  style: TextStyle(fontSize: 20, fontWeight: FontWeight.bold),
                ),
              ),

              // Список категорий
              Expanded(
                child: ListView.builder(
                  itemCount: provider.categories.length,
                  itemBuilder: (ctx, index) {
                    final cat = provider.categories[index];
                    double progress = cat.limit > 0 ? cat.spent / cat.limit : 0;
                    return Card(
                      margin: EdgeInsets.symmetric(horizontal: 16, vertical: 4),
                      child: ListTile(
                        title: Text(_getCategoryName(cat.name)),
                        subtitle: Column(
                          crossAxisAlignment: CrossAxisAlignment.start,
                          children: [
                            LinearProgressIndicator(
                              value: progress > 1 ? 1 : progress,
                              backgroundColor: Colors.grey[300],
                              color: progress >= 1 ? Colors.red : Colors.blue,
                            ),
                            SizedBox(height: 4),
                            Text(
                              '${cat.spent.toStringAsFixed(0)} / ${cat.limit.toStringAsFixed(0)} ₽ (${cat.percent.toStringAsFixed(0)}%)',
                            ),
                          ],
                        ),
                      ),
                    );
                  },
                ),
              ),

              // Кнопка для тестовой траты
              Padding(
                padding: const EdgeInsets.all(16.0),
                child: Row(
                  children: [
                    Expanded(
                      child: ElevatedButton(
                        onPressed: () => _showAddTransactionDialog(context),
                        child: Text('➕ Добавить тестовую трату'),
                      ),
                    ),
                  ],
                ),
              ),
            ],
          );
        },
      ),
    );
  }

  String _getCategoryName(String cat) {
    switch (cat) {
      case 'GROCERIES': return '🍎 Продукты';
      case 'UTILITIES': return '💡 Коммунальные';
      case 'ENTERTAINMENT': return '🎬 Развлечения';
      case 'TRANSPORT': return '🚗 Транспорт';
      case 'SAVINGS': return '💰 Накопления';
      default: return '📦 Остальное';
    }
  }

  void _showAddTransactionDialog(BuildContext context) {
    final amountController = TextEditingController();
    final descController = TextEditingController();

    showDialog(
      context: context,
      builder: (ctx) => AlertDialog(
        title: Text('Новая трата'),
        content: Column(
          mainAxisSize: MainAxisSize.min,
          children: [
            TextField(
              controller: amountController,
              decoration: InputDecoration(labelText: 'Сумма'),
              keyboardType: TextInputType.number,
            ),
            TextField(
              controller: descController,
              decoration: InputDecoration(labelText: 'Описание (например: "Магазин Пятерочка")'),
            ),
          ],
        ),
        actions: [
          TextButton(
            onPressed: () => Navigator.pop(ctx),
            child: Text('Отмена'),
          ),
          TextButton(
            onPressed: () {
              if (amountController.text.isNotEmpty && descController.text.isNotEmpty) {
                context.read<BudgetProvider>().addTestTransaction(
                  double.parse(amountController.text),
                  descController.text,
                );
                Navigator.pop(ctx);
              }
            },
            child: Text('Добавить'),
          ),
        ],
      ),
    );
  }
}