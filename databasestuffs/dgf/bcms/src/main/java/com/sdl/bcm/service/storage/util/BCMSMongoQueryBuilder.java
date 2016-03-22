package com.sdl.bcm.service.storage.util;

import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;

import com.sdl.bcm.service.storage.DocumentMetadataKeys;

public class BCMSMongoQueryBuilder {

	private final Query query;

	public BCMSMongoQueryBuilder() {
		query = new Query();
	}

	public BCMSMongoQueryBuilder addCriteriaByDocumentId(Object documentId) {
		query.addCriteria(Criteria.where(DocumentMetadataKeys.ID_QUERY_KEY).is(documentId));
		return this;
	}

	public BCMSMongoQueryBuilder addCriteriaByDocumentVersion(Object documentVersion) {
		query.addCriteria(Criteria.where(DocumentMetadataKeys.VERSION_QUERY_KEY).is(documentVersion));
		return this;
	}

	public BCMSMongoQueryBuilder addCriteriaVersionGreaterThen(Integer version) {
		query.addCriteria(Criteria.where(DocumentMetadataKeys.VERSION_QUERY_KEY).gt(version));
		return this;
	}

	public BCMSMongoQueryBuilder sortAscendingByVersion() {
		query.with(new Sort(Sort.Direction.ASC, DocumentMetadataKeys.VERSION_QUERY_KEY));
		return this;
	}

	public BCMSMongoQueryBuilder sortDescendingByVersion() {
		query.with(new Sort(Sort.Direction.DESC, DocumentMetadataKeys.VERSION_QUERY_KEY));
		return this;
	}

	public Query build() {
		return query;
	}

}
