package org.cutlery.dsl

import org.cutlery.loader.JSONLoader
import org.cutlery.loader.XMLLoader
import org.cutlery.loader.YAMLLoader
import org.cutlery.model.ColumnType
import org.cutlery.model.Table
import org.cutlery.model.column.Column
import org.cutlery.model.column.SortableColumn
import org.cutlery.model.column.TableColumn
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

class DSL(var table: Table = Table(), withMetadata: Boolean = false) {
	fun load(filename: String) {
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

			tables.add(loader.load(file))
		}

		table = Concat(tables).concat()
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
		table = ColumnSelection(table).apply(init).table
	}

	fun rows(start: Int, end: Int? = null) {
		table.columns.forEach { it.rows(start, end) }
	}

	fun unstack(columnName: String) {
		val column = table.getColumn(columnName)

		if (column.type != ColumnType.TABLE)
			throw IllegalArgumentException("Column is not a table column")

		var newTable = Table()
		val subTables: MutableList<Table> = mutableListOf()

		for (subTable in column.values) {
			subTables.add(subTable as Table)
			newTable = Concat(subTables).concat()
		}

		table = newTable
	}

	fun sort(columnName: String, ascending: Boolean = true) {
		val column = table.getColumn(columnName)

		if (column !is SortableColumn)
			throw IllegalArgumentException("Column is not a sortable column")

		column.sort(ascending)
	}
}

class ColumnSelection(var table: Table) {
	fun withName(columnName: String) {
		val newTable = Table()

		val column = table.getColumn(columnName)

		newTable.addColumn(column)

		table = newTable
	}

	fun withType(columnType: ColumnType) {
		table.columns = table.columns.filter { it.type == columnType }.toMutableList()
	}
}

fun dsl(init: DSL.() -> Unit): Table {
	return DSL().apply(init).table
}

fun merge(vararg tables: Table): DSL {
	return DSL(Merge().apply {
		tables.forEach { addTable(it) }
	}.merge())
}
