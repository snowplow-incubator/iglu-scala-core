/*
 * Copyright (c) 2016-2020 Snowplow Analytics Ltd. All rights reserved.
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
package typeclasses

/**
  * Mixin for [[ExtractSchemaMap]] marking that this particular instance intended
  * for extraction Schemas, not instances
  */
trait ToSchema[E] { self: ExtractSchemaMap[E] =>
  def toSchema(schema: E): Either[ParseError, SelfDescribingSchema[E]] =
    for {
      _ <- self.checkSchemaUri(schema)
      schemaMap <- self.extractSchemaMap(schema)
    } yield SelfDescribingSchema(schemaMap, getContent(schema))


  /** Cleanup if necessary information about schema */
  protected def getContent(entity: E): E


  protected def checkSchemaUri(entity: E): Either[ParseError, Unit]
}
