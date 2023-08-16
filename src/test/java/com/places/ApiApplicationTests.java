package com.places;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;

import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class ApiApplicationTests {

	private MediaType contentType = new MediaType(MediaType.APPLICATION_JSON.getType(),
			MediaType.APPLICATION_JSON.getSubtype(),
			Charset.forName("utf8"));

	private MockMvc mockMvc;

	private HttpMessageConverter mappingJackson2HttpMessageConverter;

	@Autowired
	private PlaceRepository placeRepository;

	@Autowired
	private WebApplicationContext webApplicationContext;

	@Autowired
	void setConverters(HttpMessageConverter<?>[] converters) {

		this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream().filter(
				hmc -> hmc instanceof MappingJackson2HttpMessageConverter).findAny().get();

		Assert.assertNotNull("the JSON message converter must not be null",
				this.mappingJackson2HttpMessageConverter);
	}

	@Before
	public void setup() throws Exception {
		this.mockMvc = webAppContextSetup(webApplicationContext).build();

		this.placeRepository.deleteAllInBatch();
	}

	@Test
	public void createPlace() throws Exception {
		String placeJson = json(new Place("Crepes & Waffles", 4.7506756, -74.09, 5.0));
		mockMvc.perform(post("/places")
				.contentType(contentType)
				.content(placeJson))
				.andExpect(status().isOk());

		mockMvc.perform(get("/places?lat=4.7506&lng=-74.09"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)));
	}

	@Test
	public void farPlacesNotCombined() throws Exception {
		String placeJson = json(new Place("Crepes & Waffles", 4.7506756, -74.09, 5.0));
		mockMvc.perform(post("/places")
				.contentType(contentType)
				.content(placeJson))
				.andExpect(status().isOk());

		placeJson = json(new Place("Crepes & Waffles", 5.7506756, -3.09, 5.0));
		mockMvc.perform(post("/places")
				.contentType(contentType)
				.content(placeJson))
				.andExpect(status().isOk());

		Assert.assertThat(placeRepository.count(), is(2L));
	}

	@Test
	public void nearPlacesWithSimilarNameAreCombined() throws Exception {
		String placeJson = json(new Place("Crepes & Waffles", 4.7506756, -74.09, 5.0));
		mockMvc.perform(post("/places")
				.contentType(contentType)
				.content(placeJson))
				.andExpect(status().isOk());

		placeJson = json(new Place("Crepes Waffles", 4.7506, -74.06, 5.0));
		mockMvc.perform(post("/places")
				.contentType(contentType)
				.content(placeJson))
				.andExpect(status().isOk());

		Assert.assertThat(placeRepository.count(), is(1L));
	}

	@Test
	public void nearPlacesWithNoSimilarNameAreNotCombined() throws Exception {
		String placeJson = json(new Place("Crepes & Waffles", 4.7506756, -74.09, 5.0));
		mockMvc.perform(post("/places")
				.contentType(contentType)
				.content(placeJson))
				.andExpect(status().isOk());

		placeJson = json(new Place("El Corral", 4.75, -74.07, 5.0));
		mockMvc.perform(post("/places")
				.contentType(contentType)
				.content(placeJson))
				.andExpect(status().isOk());

		Assert.assertThat(placeRepository.count(), is(2L));
	}

	@Test
	public void aggregateRatings() throws Exception {
		String placeJsonRating5 = json(new Place("Crepes & Waffles", 4.7506756, -74.09, 5.0));
		mockMvc.perform(post("/places")
				.contentType(contentType)
				.content(placeJsonRating5))
				.andExpect(status().isOk());

		String placeJsonRating4 = json(new Place("Crepes & Waffles", 4.7506756, -74.09, 4.0));
		mockMvc.perform(post("/places")
				.contentType(contentType)
				.content(placeJsonRating4))
				.andExpect(status().isOk());

		mockMvc.perform(post("/places")
				.contentType(contentType)
				.content(placeJsonRating4))
				.andExpect(status().isOk());

		mockMvc.perform(post("/places")
				.contentType(contentType)
				.content(placeJsonRating4))
				.andExpect(status().isOk());

		mockMvc.perform(get("/places?lat=4.7506&lng=-74.1"))
				.andExpect(status().isOk())
				.andExpect(jsonPath("$", hasSize(1)))
				.andExpect(jsonPath("$[0].rating", is(4.25)));

		Assert.assertThat(placeRepository.count(), is(1L));
	}

	@Test
	public void invalidNameRejected() throws Exception {
		String placeJsonRating4 = json(new Place("", 4.7506756, -74.09, 4.0));
		mockMvc.perform(post("/places")
				.contentType(contentType)
				.content(placeJsonRating4))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void invalidCoordintesRejected() throws Exception {
		String placeJsonRating4 = json(new Place("Wok", 400.0, -74.09, 4.0));
		mockMvc.perform(post("/places")
				.contentType(contentType)
				.content(placeJsonRating4))
				.andExpect(status().isBadRequest());
	}

	@Test
	public void invalidRatingRejected() throws Exception {
		String placeJsonRating4 = json(new Place("El corral", 4.7506756, -74.09, 400.0));
		mockMvc.perform(post("/places")
				.contentType(contentType)
				.content(placeJsonRating4))
				.andExpect(status().isBadRequest());
	}

	protected String json(Object o) throws IOException {
		MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
		this.mappingJackson2HttpMessageConverter.write(
				o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
		return mockHttpOutputMessage.getBodyAsString();
	}

}
