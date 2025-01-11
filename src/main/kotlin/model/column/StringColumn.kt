package org.cutlery.model.column

import org.cutlery.model.ColumnType

class StringColumn(name: String, values: List<String>) : Column<String>(name, values, ColumnType.STRING), SortableColumn {
	override fun sort(ascending: Boolean) {
		if (ascending)
			values.sortBy { it }
		 
		else
			values.sortByDescending { it }
	}
}