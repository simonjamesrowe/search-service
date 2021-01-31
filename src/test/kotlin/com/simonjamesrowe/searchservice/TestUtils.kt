package com.simonjamesrowe.searchservice

import com.simonjamesrowe.model.data.Image

object TestUtils {

  fun image(image: String, size: Int) =
    Image(
      url = "uploads/$image.jpg",
      name = "image1",
      size = size,
      width = size * 10,
      height = size * 10,
      mime = "jpg",
      formats = Image.ImageFormats(
        thumbnail = Image(
          url = "uploads/$image-thumb.jpg",
          name = "$image-thumb",
          size = size * 2,
          width = size * 2,
          height = size * 2,
          mime = "jpg",
          formats = null
        ),
        small = Image(
          url = "uploads/$image-sml.jpg",
          name = "$image-sml",
          size = size * 3,
          width = size * 3,
          height = size * 3,
          mime = "jpg",
          formats = null
        ),
        medium = Image(
          url = "uploads/$image-med.jpg",
          name = "$image-mde",
          size = size * 4,
          width = size * 4,
          height = size * 4,
          mime = "jpg",
          formats = null
        ),
        large = Image(
          url = "uploads/$image-lg.jpg",
          name = "$image-lg",
          size = size * 5,
          width = size * 5,
          height = size * 5,
          mime = "jpg",
          formats = null
        )
      )
    )
}
