package org.cutlery.model.column

class NumberColumn(name: String, values: List<Number>) : Column<Number>(name, values, ColumnType.NUMBER),
	SortableColumn {
	override fun sort(ascending: Boolean) {
		if (ascending)
			values.sortBy { it.toDouble() }
		else
			values.sortByDescending { it.toDouble() }
	}
}