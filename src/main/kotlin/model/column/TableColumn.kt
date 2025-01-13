package org.cutlery.model.column

import org.cutlery.model.Table

class TableColumn(name: String, values: List<Table>) : Column<Table>(name, values, ColumnType.TABLE) {
	fun unstack(): List<TableColumn> {
		val columns = mutableListOf<TableColumn>()

		values.forEach { table ->
			table.columns.forEach { column ->
				columns.add(TableColumn("${name}.${column.name}", column.values.toList() as List<Table>))
			}
		}

		return columns
	}
}