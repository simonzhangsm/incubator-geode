/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.gemstone.gemfire.internal.cache;

import com.gemstone.gemfire.compression.Compressor;

/**
 * Provides important contextual information that allows a {@link RegionEntry} to manage its state.
 * @since 8.0
 */
public interface RegionEntryContext extends HasCachePerfStats {
  public static final String DEFAULT_COMPRESSION_PROVIDER="com.gemstone.gemfire.compression.SnappyCompressor";
  
  /**
   * Returns the compressor to be used by this region entry when storing the
   * entry value.
   * 
   * @return null if no compressor is assigned or available for the entry.
   */
  public Compressor getCompressor();
  /**
   * Returns true if region entries are stored off heap.
   */
  public boolean getOffHeap();
}
