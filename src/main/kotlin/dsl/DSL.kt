package org.cutlery.dsl

import org.cutlery.loader.JSONLoader
import org.cutlery.loader.XMLLoader
import org.cutlery.loader.YAMLLoader
import org.cutlery.model.Table
import org.cutlery.model.column.*
import org.cutlery.saver.JSONSaver
import org.cutlery.utils.GlobFileVisitor
import java.io.File
import java.nio.file.Files
import java.nio.file.Paths

class Merge(private var tables: MutableList<Table> = mutableListOf()) {
	fun addTable(table: Table) {
		tables.add(table)
	}

	fun merge(): Table {
		val newTable = Table()

		tables.forEach { table ->
			table.columns.forEach { column: Column<*> ->
				newTable.addColumn(column)
			}
		}

		return newTable
	}
}

class Concat(private var tables: MutableList<Table> = mutableListOf()) {
	fun addTable(table: Table) {
		tables.add(table)
	}

	fun concat(): Table {
		val newTable = tables.first()

		for (table in tables.subList(1, tables.size))
			newTable.concat(table)

		return newTable
	}
}

class TableBuilder(var table: Table = Table()) {
	fun load(filename: String, withMetadata: Boolean = false) {
		val fileVisitor = GlobFileVisitor(filename)

		Files.walkFileTree(Paths.get("src/test/resources"), fileVisitor)

		val paths = fileVisitor.getMatchedFiles()
		val tables = mutableListOf<Table>()

		for (path in paths) {
			val file = path.toFile()
			val loader = when (file.extension) {
				"json" -> JSONLoader()
				"xml" -> XMLLoader()
				"yaml" -> YAMLLoader()

				else -> throw IllegalArgumentException("Unsupported file extension")
			}

			val loadedTable = loader.load(file)

			if (withMetadata)
				loadedTable.addColumn(StringColumn("File", listOf(file.name)))

			tables.add(loadedTable)
		}

		if (tables.size == 1)
			table = tables.first()
		else {
			tables.forEachIndexed { index, value ->
				val column = TableColumn(index.toString(), listOf(value))

				table.addColumn(column)
			}
		}
	}

	fun save(filename: String) {
		val file = File(filename)
		val saver = when (file.extension) {
			"json" -> JSONSaver()

			else -> throw IllegalArgumentException("Unsupported file extension")
		}

		saver.save(table, file)
	}

	fun columns(init: ColumnSelection.() -> Unit) {
		table = ColumnSelection(table).apply(init).result()
	}

	fun rows(start: Int, end: Int? = null) {
		table.columns.forEach { it.rows(start, end) }
	}

	fun extract(columnName: String): Table {
		val columnToExtract = table.getColumn(columnName)

		if (columnToExtract.type != ColumnType.TABLE)
			throw IllegalArgumentException("Column is not a table column")

		var newTable = Table()
		val subTables: MutableList<Table> = mutableListOf()

		for (subTable in columnToExtract.values) {
			subTables.add(subTable as Table)
			newTable = Concat(subTables).concat()
		}

		table.removeColumn(columnName)

		for (column in newTable.columns)
			table.addColumn(column)

		return newTable
	}

	fun sort(columnName: String, ascending: Boolean = true) {
		val column = table.getColumn(columnName)

		if (column !is SortableColumn)
			throw IllegalArgumentException("Column is not a sortable column")

		column.sort(ascending)
	}

	fun unstack() {
		val newTable = Table()

		table.columns.forEach { column ->
			if (column.type != ColumnType.TABLE)
				throw IllegalArgumentException("Column is not a table column")

			val subTable = column.values[0] as Table

			subTable.columns.forEach {
				if (!newTable.hasColumn(it.name))
					newTable.addColumn(it)
				else
					for (value in it.values)
						newTable.getColumn(it.name).add(value)
			}
		}

		table = newTable
	}

	fun stack() {
		val newTable = Table()

		for (i in 0 until table.columns[0].size()) {
			val newColumn = TableColumn(i.toString(), mutableListOf())
			val subTable = Table()

			for (column in table.columns)
				subTable.addColumn(column.slice(i))

			newColumn.add(subTable)
			newTable.addColumn(newColumn)
		}

		table = newTable
	}

	fun rename(columnName: String, newColumnName: String) {
		val column = table.getColumn(columnName)

		column.name = newColumnName
	}

	fun unravel() {
		val columnNames = table.columns.map { it.name }

		for ((i, columnName) in columnNames.withIndex()) {
			when (table.getColumn(columnName)) {
				is TableColumn -> {
					val extractedTable = extract(columnName)

					for (column in extractedTable.columns)
						rename(column.name, "${column.name} #${i + 1}")
				}
			}

		}
	}

	fun forEach(init: TableBuilder.() -> Unit) {
		table.columns.forEach {
			val newTable = TableBuilder(it.get(0) as Table).apply(init).table

			it.clear()
			it.add(newTable)
		}
	}
}

class ColumnSelection(val table: Table) {
	private val columns = mutableListOf<Column<*>>()

	fun withName(vararg columnName: String) {
		columns.addAll(table.columns.filter { it.name in columnName }.toMutableList())
	}

	fun withType(columnType: ColumnType, invert: Boolean = false) {
		columns.addAll(table.columns.filter { (it.type == columnType) xor invert }.toMutableList())
	}

	fun result(): Table {
		val newTable = Table()

		columns.forEach { column ->
			newTable.addColumn(column)
		}

		return newTable
	}
}

fun table(init: TableBuilder.() -> Unit): Table {
	return TableBuilder().apply(init).table
}

fun merge(vararg tables: Table): TableBuilder {
	return TableBuilder(Merge().apply {
		tables.forEach { addTable(it) }
	}.merge())
}

fun concat(vararg tables: Table): TableBuilder {
	return TableBuilder(Concat().apply {
		tables.forEach { addTable(it) }
	}.concat())
}