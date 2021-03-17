package com.simonjamesrowe.searchservice

import com.github.tomakehurst.wiremock.WireMockServer
import com.github.tomakehurst.wiremock.client.WireMock
import com.simonjamesrowe.model.cms.dto.BlogResponseDTO
import com.simonjamesrowe.model.cms.dto.ImageResponseDTO
import com.tyro.oss.arbitrater.arbitrary
import org.jeasy.random.EasyRandom
import org.jeasy.random.EasyRandomParameters
import org.springframework.core.io.ClassPathResource
import java.nio.file.Files
import java.util.stream.Collectors

object TestUtils {

  fun image(image: String, size: Int) =
    ImageResponseDTO(
      url = "uploads/$image.jpg",
      name = "image1",
      size = size,
      width = size * 10,
      height = size * 10,
      mime = "jpg",
      formats = ImageResponseDTO.ImageFormats(
        thumbnail = ImageResponseDTO(
          url = "uploads/$image-thumb.jpg",
          name = "$image-thumb",
          size = size * 2,
          width = size * 2,
          height = size * 2,
          mime = "jpg",
          formats = null
        ),
        small = ImageResponseDTO(
          url = "uploads/$image-sml.jpg",
          name = "$image-sml",
          size = size * 3,
          width = size * 3,
          height = size * 3,
          mime = "jpg",
          formats = null
        ),
        medium = ImageResponseDTO(
          url = "uploads/$image-med.jpg",
          name = "$image-mde",
          size = size * 4,
          width = size * 4,
          height = size * 4,
          mime = "jpg",
          formats = null
        ),
        large = ImageResponseDTO(
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

  fun mockGet(wireMockServer: WireMockServer, uri: String, responseBodyFile: String) {
    wireMockServer.addStubMapping(
      WireMock.stubFor(
        WireMock.get(WireMock.urlEqualTo(uri))
          .willReturn(
            WireMock.aResponse()
              .withHeader("Content-Type", "application/json")
              .withBody(
                Files.lines(
                  ClassPathResource(responseBodyFile).file.toPath()
                ).collect(Collectors.joining(System.lineSeparator()))
              )
          )
      )
    )
  }

  inline fun <reified T> randomObject(args : Map<String, Any> = mapOf()) : T {
    var parameters = EasyRandomParameters()
    args.forEach{ param ->
      parameters = parameters.randomize({ it.name == param.key}, { param.value })
    }

    return EasyRandom(parameters).nextObject(T::class.java)
  }

}
