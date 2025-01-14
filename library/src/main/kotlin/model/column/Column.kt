package org.cutlery.model.column

import org.cutlery.model.Table

abstract class Column<T>(var name: String, values: List<T>, val type: ColumnType) {
	var values = values.toMutableList()

	fun get(index: Int): T {
		return values[index]
	}

	fun add(value: Any?) {
		values.add(value as T)
	}

	fun size(): Int {
		return values.size
	}

	fun isEmpty(): Boolean {
		return values.isEmpty()
	}

	fun forEach(action: (T) -> Unit) {
		values.forEach(action)
	}

	fun rows(start: Int, end: Int? = null) {
		values = values.subList(start, end ?: (start + 1))
	}

	fun slice(start: Int, end: Int? = null): Column<*> {
		return when (type) {
			ColumnType.NUMBER -> NumberColumn(name, values.subList(start, end ?: (start + 1)).map { it as Double })
			ColumnType.STRING -> StringColumn(name, values.subList(start, end ?: (start + 1)).map { it as String })
			ColumnType.TABLE -> TableColumn(name, values.subList(start, end ?: (start + 1)).map { it as Table })
		}
	}

	fun clear() {
		values.clear()
	}
}