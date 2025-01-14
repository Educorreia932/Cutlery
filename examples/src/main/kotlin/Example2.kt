package org.cutlery

import org.cutlery.dsl.merge
import org.cutlery.dsl.table
import org.cutlery.model.column.ColumnType

fun main() {
	val resourcesLocation = "src/main/resources"
	val folder = "${resourcesLocation}/example2"

	merge(
		table {
			load("${folder}/vitis-report.xml")
			extract("AreaEstimates")
			columns { withName("Resources") }
		},
		table {
			load("${folder}/decision_tree.yaml")
			columns {
				withType(ColumnType.TABLE, true)
				withName("params")
			}
		},
		table {
			load("${folder}/profiling.json")
			extract("functions")
			unstack()
			columns { withName("name", "time%") }
			sort("time%", false)
			rows(0)
		},
	)
		.save("example2.json")
}
