package ru.fintech.kotlin.utils.json.impl

import ru.fintech.kotlin.location.dto.LocationSerializableDto
import ru.fintech.kotlin.utils.json.JsonParser

class LocationJsonParser : JsonParser<LocationSerializableDto>(LocationSerializableDto.serializer())