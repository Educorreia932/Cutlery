package org.cutlery.model

import org.cutlery.model.column.Column
import org.cutlery.model.column.NumberColumn
import org.cutlery.model.column.StringColumn
import org.cutlery.model.column.TableColumn

class Table {
	var columns = mutableListOf<Column<*>>() // TODO: Make this map (?)

	fun addColumn(column: Column<*>) {
		columns.add(column)
	}

	fun getColumn(columnName: String): Column<*> {
		return columns.find { it.name == columnName } ?: throw IllegalArgumentException("Column not found")
	}
	
	fun hasColumn(columnName: String): Boolean {
		return columns.any { it.name == columnName }
	}

	fun removeColumn(columnName: String) {
		columns.removeIf { it.name == columnName }
	}

	fun concat(other: Table) {
		for (otherColumn in other.columns) {
			val column = columns.find { it.name == otherColumn.name }

			for (value in otherColumn.values)
				column?.add(value)
		}
	}

	companion object {
		fun fromMap(map: Map<String, Any>): Table {
			return buildTable(map)
		}

		private fun buildTable(map: Map<String, Any>): Table {
			val table = Table()

			map.forEach { (key, value) ->
				val column = buildColumn(key, value)

				table.addColumn(column)
			}

			return table
		}

		private fun buildArrayTable(values: List<*>): Table {
			val table = Table()

			values.forEachIndexed { index, value ->
				val column = buildColumn(index.toString(), value!!)

				table.addColumn(column)
			}

			return table
		}

		private fun buildColumn(key: String, value: Any): Column<*> {
			return when (value) {
				is String -> {
					if (value.toDoubleOrNull() != null)
						NumberColumn(key, listOf(value.toDouble()))
					else
						StringColumn(key, listOf(value))
				}

				is Int -> NumberColumn(key, listOf(value))
				is Float -> NumberColumn(key, listOf(value))
				is Double -> NumberColumn(key, listOf(value))
				is List<*> -> TableColumn(key, listOf(buildArrayTable(value)))
				is Map<*, *> -> TableColumn(key, listOf(buildTable(value as Map<String, Any>)))
				else -> StringColumn(key, listOf())
			}
		}
	}
}