import org.cutlery.dsl.concat
import org.cutlery.dsl.table
import org.cutlery.dsl.merge
import org.cutlery.model.column.ColumnType
import kotlin.test.Test

class Tests {
	private val resourcesLocation = "src/test/resources"

	@Test
	fun `Example 1`() {
		val folder = "${resourcesLocation}/example1"

		concat(
			table {
				load("${folder}/decision_tree_1.yaml", true)
			},
			table {
				load("${folder}/decision_tree_2.yaml", true)
			},
			table {
				load("${folder}/decision_tree_3.yaml", true)
			}
		).apply {
			extract("params")
			columns { withName("criterion", "splitter", "ccp_alpha", "min_samples_split", "File") }
			rename("criterion", "Criterion")
			rename("splitter", "Splitter")
			rename("ccp_alpha", "CCP Alpha")
			rename("min_samples_split", "Min Samples Split")
			save("example1.json")
		}
	}

	@Test
	fun `Example 2`() {
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

	@Test
	fun `Example 3`() {
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
}
