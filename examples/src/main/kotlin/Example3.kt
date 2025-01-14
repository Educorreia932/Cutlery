package org.cutlery

import org.cutlery.dsl.merge
import org.cutlery.dsl.table
import org.cutlery.model.column.ColumnType

fun main() {
	val resourcesLocation = "src/main/resources"
	val folder = "${resourcesLocation}/example3"

	merge(
		table {
			load("${folder}/**/analysis.yaml")
			forEach {
				extract("total")
				extract("results")
				columns { withName("dynamic") }
				extract("dynamic")
				rename("iterations", "Iterations (Dynamic)")
				rename("calls", "Calls (Dynamic)")
			}
		},
		table {
			load("${folder}/**/analysis.xml")
			forEach {
				extract("total")
				extract("results")
				columns { withName("static") }
				extract("static")
				rename("nodes", "Nodes (Static)")
				rename("functions", "Functions (Static)")
			}
		},
		table {
			load("${folder}/**/profiling.json")
			forEach {
				extract("functions")
				unstack()
				rows(0, 3)
				columns { withName("name", "time%") }
				stack()
				unravel()
			}
		}
	).apply {
		unstack()
		aggregate {
			sum()
			average()
		}
		save("example3.json")
	}
}
