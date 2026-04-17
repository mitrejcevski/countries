package nl.jovmit.countries.ui.screen

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

data class ProductUiModel(
    val id: String,
    val title: String,
    val price: String,
    val isSponsored: Boolean,
    val isInStock: Boolean
)

private val products = listOf(
    ProductUiModel("1", "Item 1", "1", false, false),
    ProductUiModel("2", "Item 2", "2", false, true),
    ProductUiModel("3", "Item 3", "3", true, false),
    ProductUiModel("4", "Item 4", "4", true, true),
)

@Composable
fun SearchScreen() {
    Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
    ) {
        Text(text = "Search Results")
        Spacer(modifier = Modifier.height(12.dp))

        products.forEach { product ->
            var added by remember { mutableStateOf(false) }

            ListItem(
                productInfo = ProductInfo.Search(product),
                selected = added,
                onClickButton = { added = !added }
            )
        }
    }
}

@Composable
fun DealsScreen(loading: Boolean) {
    Column(
        modifier = Modifier
          .fillMaxSize()
          .padding(16.dp)
    ) {
        Text(text = "Deals")
        Spacer(modifier = Modifier.height(12.dp))

        if (loading) {
            CircularProgressIndicator()
        } else {
            products.forEach { product ->
                var favorite by remember { mutableStateOf(false) }

                ListItem(
                    productInfo = ProductInfo.Deal(product),
                    selected = favorite,
                    onClickButton = { favorite = !favorite }
                )
            }
        }
    }
}


sealed class ProductInfo(open val product: ProductUiModel) {
    data class Search(override val product: ProductUiModel) : ProductInfo(product)
    data class Deal(override val product: ProductUiModel) : ProductInfo(product)
}

@Composable
private fun ListItem(
    productInfo: ProductInfo,
    selected: Boolean,
    onClickButton: () -> Unit
) {
    when (val info = productInfo) {

        is ProductInfo.Search -> {

            Column(
                modifier = Modifier
                  .fillMaxWidth()
                  .padding(bottom = 12.dp)
                  .background(Color.LightGray, RoundedCornerShape(12.dp))
                  .clickable { println("open product ${info.product.id}") }
                  .padding(16.dp)
            ) {
                if (info.product.isSponsored) {
                    Text(text = "Sponsored", color = Color.Red)
                    Spacer(modifier = Modifier.height(8.dp))
                }

                Text(text = info.product.title)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = info.product.price)

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    textLabel = if (info.product.isInStock) {
                        if (selected) "Added" else "Add to cart"
                    } else {
                        "Out of stock"
                    },
                    color = if (info.product.isInStock) Color.Blue else Color.Gray,
                    onClick = {
                        if (info.product.isInStock)
                            onClickButton()
                    },
                    enabled = info.product.isInStock
                )
            }
        }

        is ProductInfo.Deal -> {


                Text(text = info.product.title)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = info.product.price)

                Spacer(modifier = Modifier.height(12.dp))

                TextButton(
                    textLabel = if (selected) "Wishlisted" else "Add to wishlist",
                    color = if (selected) Color.Magenta else Color.DarkGray,
                    onClick = { onClickButton() }
                )
            }

        }
    }

}

@Composable
private fun TextButton(
    textLabel: String,
    color: Color,
    onClick: () -> Unit = {},
    enabled: Boolean = true
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
          .background(
            color,
            RoundedCornerShape(8.dp)
          )
          .clickable(enabled) {
            onClick()
          }
          .padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Text(
            text = textLabel,
            color = Color.White
        )
    }
}

@Preview
@Composable
private fun PreviewSearch() {
    Column(
        modifier = Modifier
            .background(Color.White)
    ) {
        SearchScreen()
    }
}

@Preview
@Composable
private fun PreviewDeals() {
    Column(
        modifier = Modifier
            .background(Color.White)
    ) {
        DealsScreen(loading = false)
    }
}