package hymnal.sing.hymn

import android.content.Context
import androidx.appcompat.widget.AppCompatTextView
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.PreviewLightDark
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.res.ResourcesCompat
import androidx.core.text.HtmlCompat
import hymnal.sing.hymn.model.HymnContent
import io.noties.markwon.Markwon
import libraries.hymnal.ui.HymnalTheme
import libraries.hymnal.ui.toAndroidColor
import library.hymnal.fonts.R as FontsR

@Composable
fun HymnText(content: HymnContent, prefs: DisplayPrefs, modifier: Modifier = Modifier) {
    when (content) {
        is HymnContent.Html -> HtmlText(content.content, prefs, modifier)
        is HymnContent.Markdown -> MarkdownText(content.content, prefs, modifier)
    }
}

@Composable
private fun HtmlText(
    text: String,
    prefs: DisplayPrefs,
    modifier: Modifier = Modifier,
) {
    val spannedText by remember {
        mutableStateOf(HtmlCompat.fromHtml(text, 0))
    }
    val textColor = MaterialTheme.colorScheme.onSurface

    AndroidView(
        modifier = modifier,
        factory = {
            createTextView(it, prefs.copy(color = textColor.toAndroidColor()))
        },
        update = {
            it.text = spannedText
        }
    )
}

@Composable
private fun MarkdownText(
    text: String,
    prefs: DisplayPrefs,
    modifier: Modifier = Modifier,
) {
    val context = LocalContext.current
    val markwon by remember { mutableStateOf(Markwon.create(context)) }
    val textColor = MaterialTheme.colorScheme.onSurface

    AndroidView(
        modifier = modifier,
        factory = {
            createTextView(it, prefs.copy(color = textColor.toAndroidColor()))
        },
        update = {
            markwon.setMarkdown(it, text)
        }
    )
}

private fun createTextView(
    context: Context,
    prefs: DisplayPrefs,
    ): AppCompatTextView {
    return AppCompatTextView(context).apply {
        setTextIsSelectable(true)
        textSize = prefs.fontSize
        typeface = ResourcesCompat.getFont(context, prefs.fontRes)
        setTextColor(prefs.color)
    }
}

@PreviewLightDark
@Composable
private fun HymnTextPreview() {
    val prefs = DisplayPrefs(FontsR.font.proxima_nova_soft_regular, 18f)

    HymnalTheme {
        Surface {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
            ) {
                HymnText(
                    content = HymnContent.Html("<h1>HTML</h1>\n<p>This is a test</p>"),
                    prefs = prefs,
                )

                HymnText(
                    content = HymnContent.Markdown("# Markdown\nThis is a test"),
                    prefs = prefs,
                )
            }
        }
    }
}