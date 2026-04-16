package nl.jovmit.countries

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

      Column(
        modifier = Modifier
          .fillMaxWidth()
          .padding(bottom = 12.dp)
          .background(Color.LightGray, RoundedCornerShape(12.dp))
          .clickable { println("open product ${product.id}") }
          .padding(16.dp)
      ) {
        if (product.isSponsored) {
          Text(text = "Sponsored", color = Color.Red)
          Spacer(modifier = Modifier.height(8.dp))
        }

        Text(text = product.title)
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = product.price)

        Spacer(modifier = Modifier.height(12.dp))

        if (product.isInStock) {
          Row(
            modifier = Modifier
              .background(Color.Blue, RoundedCornerShape(8.dp))
              .clickable { added = !added }
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (added) "Added" else "Add to cart",
              color = Color.White
            )
          }
        } else {
          Row(
            modifier = Modifier
              .background(Color.Gray, RoundedCornerShape(8.dp))
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(text = "Out of stock", color = Color.White)
          }
        }
      }
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

        Column(
          modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 12.dp)
            .background(Color.LightGray, RoundedCornerShape(12.dp))
            .clickable { println("open deal ${product.id}") }
            .padding(16.dp)
        ) {
          Text(text = product.title)
          Spacer(modifier = Modifier.height(8.dp))
          Text(text = product.price)

          Spacer(modifier = Modifier.height(12.dp))

          Row(
            modifier = Modifier
              .background(
                if (favorite) Color.Magenta else Color.DarkGray,
                RoundedCornerShape(8.dp)
              )
              .clickable { favorite = !favorite }
              .padding(horizontal = 12.dp, vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
          ) {
            Text(
              text = if (favorite) "Wishlisted" else "Add to wishlist",
              color = Color.White
            )
          }
        }
      }
    }
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