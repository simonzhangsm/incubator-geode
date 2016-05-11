/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package com.gemstone.gemfire.cache.lucene.internal;

import static org.junit.Assert.*;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.gemstone.gemfire.cache.Region;
import com.gemstone.gemfire.cache.RegionShortcut;
import com.gemstone.gemfire.cache.lucene.CreateCache;
import com.gemstone.gemfire.cache.lucene.LuceneService;
import com.gemstone.gemfire.cache.lucene.LuceneServiceProvider;
import com.gemstone.gemfire.cache.lucene.internal.repository.IndexRepository;
import com.gemstone.gemfire.cache.lucene.internal.repository.IndexRepositoryImpl;
import com.gemstone.gemfire.cache.lucene.internal.repository.RepositoryManager;
import com.gemstone.gemfire.internal.cache.BucketNotFoundException;
import com.gemstone.gemfire.internal.cache.LocalRegion;
import com.gemstone.gemfire.test.junit.categories.IntegrationTest;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.core.KeywordAnalyzer;
import org.apache.lucene.analysis.miscellaneous.PerFieldAnalyzerWrapper;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.junit.Rule;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.rules.TestName;

@Category(IntegrationTest.class)
public class LuceneIndexCreationIntegrationTest extends LuceneIntegrationTest {
  public static final String INDEX_NAME = "index";
  public static final String REGION_NAME = "region";

  @Test
  public void shouldCreateIndexWriterWithAnalyzersWhenSettingPerFieldAnalyzers() throws BucketNotFoundException {
    Map<String, Analyzer> analyzers = new HashMap<>();
    analyzers.put("field1", new KeywordAnalyzer());
    analyzers.put("field2", new StandardAnalyzer());
    luceneService.createIndex(INDEX_NAME, REGION_NAME, analyzers);
    Region region = createRegion();
    region.put("key1", new TestObject());

    LuceneIndexForPartitionedRegion index = (LuceneIndexForPartitionedRegion) luceneService.getIndex(INDEX_NAME, REGION_NAME);

    final RepositoryManager repoManager = index
      .getRepositoryManager();
    final IndexRepositoryImpl repository = (IndexRepositoryImpl) repoManager.getRepository(
      region, "key1", null);
    final PerFieldAnalyzerWrapper foundAnalyzer = (PerFieldAnalyzerWrapper) repository.getWriter().getAnalyzer();
  }

  @Test
  public void verifyLuceneRegionInternal() {
    createIndex("text");

    // Create partitioned region
    createRegion();

    // Get index
    LuceneIndexForPartitionedRegion index = (LuceneIndexForPartitionedRegion) luceneService.getIndex(INDEX_NAME, REGION_NAME);

    // Verify the meta regions exist and are internal
    LocalRegion chunkRegion = (LocalRegion) cache.getRegion(index.createChunkRegionName());
    assertTrue(chunkRegion.isInternalRegion());
    LocalRegion fileRegion = (LocalRegion) cache.getRegion(index.createFileRegionName());
    assertTrue(fileRegion.isInternalRegion());

    // Verify the meta regions are not contained in the root regions
    for (Region region : cache.rootRegions()) {
      assertNotEquals(chunkRegion.getFullPath(), region.getFullPath());
      assertNotEquals(fileRegion.getFullPath(), region.getFullPath());
    }
  }

  private Region createRegion() {
    return this.cache.createRegionFactory(RegionShortcut.PARTITION).create(REGION_NAME);
  }

  private void createIndex(String fieldName) {
    LuceneServiceProvider.get(this.cache).createIndex(INDEX_NAME, REGION_NAME, fieldName);
  }

  private static class TestObject implements Serializable {

    String field1 = "a b c d";
    String field2 = "f g h";
  }
}
