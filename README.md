# AirGraph
Basic custom graph view implementation for line/area charts

# Goals
This library facilitates the creation of line and area charts using Canvas.

Having tried quite a few libraries to build graphs, the author came to the conclusion that the comfiest tool to build a graph is the bare bone Android Canvas along with Paint and Path.
Hence the mere goals of this library are to:

 - give a primer of Canvas use
 - give an primer of Kotlin use in Android
 - help kickstart an own Canvas graph

# Usage
Fork this repo and import it into the project as a dependent module **or** download the .kt and .xml files and put them into a project.

There are no third party dependencies.

1. Add the GraphView into a fragment

```xml
<air.graph.line.GraphView
        android:id="@+id/weather_report_graph"
        style="@style/RemainderVertical.AirGraph"
        app:graph_title="Weather Report"/>
```

2. The style `RemainderVertical.AirGraph` is defined in the `styles.xml`

```xml
<style name="RemainderVertical">
    <item name="android:layout_width">wrap_content</item>
    <item name="android:layout_height">0dp</item>
    <item name="android:layout_weight">1</item>
</style>

<!-- AirGraph default settings -->
<style name="RemainderVertical.AirGraph">
    <item name="android:background">@color/graph_background</item>
    <item name="line_color">@color/graph_line</item>
    <item name="area_color">@color/graph_area</item>
    <item name="grid_color">@color/graph_grid</item>
    <item name="text_color">@color/graph_text</item>
    <item name="mark_color">@color/graph_mark</item>
    <item name="title_text_size">13sp</item>
    <item name="title_x_offset">10dp</item>
    <item name="title_y_offset">10dp</item>
    <item name="label_text_size">10sp</item>
    <item name="line_width">2dp</item>
    <item name="grid_line_width">1dp</item>
    <item name="end_point_marker_radius">5dp</item>
    <item name="end_point_label_text_size">20sp</item>
    <item name="end_point_label_x_offset">10dp</item>
    <item name="end_point_label_y_offset">20dp</item>
    <item name="vertical_offset">50dp</item>
    <item name="horizontal_offset">0dp</item>
</style>
```

The colors used in the style definition are available in the `airgraph_colors.xml` file in the project

3. Hook up to the AirGraph view in the Fragment `onCreateView` method

```kotlin
var weatherReportGraph: GraphView? = null

override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
    val view = inflater.inflate(..., container, false)

    weatherReportGraph = view.findViewById(R.id.weather_report_graph) as GraphView

    return view
}
```

4. Fill the values of the graph. For instance, here's the weather report for the past few days.

```kotlin
val temperatureMap = mapOf(
        "Mar 14" to 36f,
        "Mar 15" to 35f,
        "Mar 16" to 35f,
        "Mar 17" to 33f
)

weatherReportGraph?.labels = temperatureMap.map { entry -> entry.key }
weatherReportGraph?.values = temperatureMap.values().toArrayList()
weatherReportGraph?.endPointLabel = "${temperatureMap.entrySet().last().getValue()} °C"
```

5. Invalidate the View at `onResume()` to trigger Android to draw the View

```kotlin
weatherReportGraph?.invalidate()
```

# Properties description
Here's a cheatsheet to show all the XML or Java/Kotlin properties.

There are three layout/style irrelevant properties: `values`, `labels` and `endPointLabel` - these must be set up in the code.


![airgraph properties cheatsheet](https://cloud.githubusercontent.com/assets/758512/6677666/dacae49c-cc3a-11e4-8d88-3ea51c4865b0.png)
