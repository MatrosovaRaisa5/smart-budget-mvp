import 'dart:convert';
import 'package:flutter/material.dart';
import 'package:http/http.dart' as http;

// Модель для одной категории
class CategoryData {
  final String name;
  final double limit;
  final double spent;
  final double remaining;
  final double percent;

  CategoryData({
    required this.name,
    required this.limit,
    required this.spent,
    required this.remaining,
    required this.percent,
  });
}

class BudgetProvider extends ChangeNotifier {
  double _income = 0;
  List<CategoryData> _categories = [];
  List<String> _alerts = [];
  bool _isLoading = false;

  // Геттеры для доступа к данным извне
  double get income => _income;
  List<CategoryData> get categories => _categories;
  List<String> get alerts => _alerts;
  bool get isLoading => _isLoading;

  final String baseUrl = 'http://10.58.245.61:8080/api/budget';

  // Загрузка статуса с сервера
  Future<void> loadStatus() async {
    _isLoading = true;
    notifyListeners();
    try {
      final response = await http.get(Uri.parse('$baseUrl/status'));
      if (response.statusCode == 200) {
        final Map<String, dynamic> data = json.decode(response.body);
        _income = data['income'] ?? 0;
        _categories = [];
        if (data['categories'] != null) {
          (data['categories'] as Map<String, dynamic>).forEach((key, value) {
            _categories.add(CategoryData(
              name: key,
              limit: value['limit']?.toDouble() ?? 0,
              spent: value['spent']?.toDouble() ?? 0,
              remaining: value['remaining']?.toDouble() ?? 0,
              percent: value['percent']?.toDouble() ?? 0,
            ));
          });
        }
        // Загружаем алерты
        final alertsResponse = await http.get(Uri.parse('$baseUrl/alerts'));
        if (alertsResponse.statusCode == 200) {
          _alerts = List<String>.from(json.decode(alertsResponse.body));
        }
      }
    } catch (e) {
      print("Ошибка загрузки: $e");
    }
    _isLoading = false;
    notifyListeners();
  }

  // Отправка настроек бюджета
  Future<void> setupBudget(double income, Map<String, double> percentages) async {
    try {
      final response = await http.post(
        Uri.parse('$baseUrl/setup'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'income': income,
          'percentages': percentages,
        }),
      );
      print("Ответ от сервера: ${response.body}");
      await loadStatus(); // Перезагружаем данные после сохранения
    } catch (e) {
      print("Ошибка сохранения: $e");
    }
  }

  // Добавление тестовой транзакции 
  Future<void> addTestTransaction(double amount, String desc) async {
    try {
      await http.post(
        Uri.parse('$baseUrl/transaction'),
        headers: {'Content-Type': 'application/json'},
        body: json.encode({
          'amount': amount,
          'description': desc,
        }),
      );
      await loadStatus(); // Обновляем данные после траты
    } catch (e) {
      print("Ошибка добавления транзакции: $e");
    }
  }
}