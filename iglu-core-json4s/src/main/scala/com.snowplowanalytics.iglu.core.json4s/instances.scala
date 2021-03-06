/*
 * Copyright (c) 2016-2019 Snowplow Analytics Ltd. All rights reserved.
 *
 * This program is licensed to you under the Apache License Version 2.0,
 * and you may not use this file except in compliance with the Apache License Version 2.0.
 * You may obtain a copy of the Apache License Version 2.0 at http://www.apache.org/licenses/LICENSE-2.0.
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the Apache License Version 2.0 is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the Apache License Version 2.0 for the specific language governing permissions and limitations there under.
 */
package com.snowplowanalytics.iglu.core
package json4s

import org.json4s._
import org.json4s.jackson.JsonMethods.compact

import com.snowplowanalytics.iglu.core.typeclasses._

trait instances {

  private implicit val codecs = Json4sIgluCodecs.formats

  final implicit val igluAttachToDataJValue: ExtractSchemaKey[JValue] with ToData[JValue] =
    new ExtractSchemaKey[JValue] with ToData[JValue] {

      def extractSchemaKey(entity: JValue) =
        entity \ "schema" match {
          case JString(schema) => SchemaKey.fromUri(schema)
          case _               => Left(ParseError.InvalidData)
        }

      def getContent(json: JValue): Either[ParseError, JValue] =
        json \ "data" match {
          case JNothing => Left(ParseError.InvalidData)
          case data => Right(data)
        }
    }

  final implicit val igluAttachToSchema: ExtractSchemaMap[JValue] with ToSchema[JValue] with ExtractSchemaMap[JValue] =
    new ToSchema[JValue] with ExtractSchemaMap[JValue] {

      def extractSchemaMap(entity: JValue): Either[ParseError, SchemaMap] =
        (entity \ "self").extractOpt[SchemaKey].map(key => SchemaMap(key)) match {
          case Some(map) => Right(map)
          case None => Left(ParseError.InvalidSchema)
        }

      def checkSchemaUri(entity: JValue): Either[ParseError, Unit] = {
        (entity \ "$schema").extractOpt[String] match {
          case Some(schemaUri) if schemaUri ==  SelfDescribingSchema.SelfDescribingUri.toString => Right(())
          case _ => Left(ParseError.InvalidMetaschema)
        }
      }

      def getContent(schema: JValue): JValue =
        Json4sIgluCodecs.removeMetaFields(schema)
    }

  // Container-specific instances

  final implicit val igluNormalizeDataJValue: NormalizeData[JValue] =
    instance => Extraction.decompose(instance)

  final implicit val igluNormalizeSchemaJValue: NormalizeSchema[JValue] =
    schema => Extraction.decompose(schema)

  final implicit val igluStringifyDataJValue: StringifyData[JValue] =
    container => compact(container.normalize(igluNormalizeDataJValue))

  final implicit val igluStringifySchemaJValue: StringifySchema[JValue] =
    container => compact(container.normalize(igluNormalizeSchemaJValue))
}

object instances extends instances
