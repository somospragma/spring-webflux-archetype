package com.scheme.reactive.scheme_reactive.repository;

import org.springframework.beans.factory.annotation.Autowired;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.Key;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbIndex;
import software.amazon.awssdk.enhanced.dynamodb.Expression;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.enhanced.dynamodb.model.Page;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryConditional;
import software.amazon.awssdk.enhanced.dynamodb.model.QueryEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Optional;

public abstract class BaseRepository<I> {

    private String tableName;
    private Boolean consistentRead;
    private DynamoDbEnhancedClient dynamoDbenhancedClient;

    @Autowired
    public void setDynamoDbenhancedClient(DynamoDbEnhancedClient dynamoDbenhancedClient) {
        this.dynamoDbenhancedClient = dynamoDbenhancedClient;
    }

    protected void save(final I item) {
        DynamoDbTable<I> orderTable = getTable();
        orderTable.putItem(item);
    }

    protected void update(final I item) {
        DynamoDbTable<I> orderTable = getTable();
        orderTable.updateItem(item);
    }

    private DynamoDbTable<I> getTable() {
        return dynamoDbenhancedClient.table(getTableName(),
                TableSchema.fromBean((Class<I>) ((ParameterizedType) this.getClass().getGenericSuperclass()).getActualTypeArguments()[0]));
    }

    protected <T, V> I getItemByPartitionStringSk(final T pk, final Optional<V> sk) {
        DynamoDbTable<I> table = getTable();
        Key.Builder key = Key.builder().partitionValue((AttributeValue) pk);
        sk.ifPresent(v -> key.sortValue((String) v));
        return table.getItem(GetItemEnhancedRequest.builder().consistentRead(getConsistentRead()).key(key.build()).build());
    }

    protected <V> I getItem(String pk, final Optional<V> sk) {
        DynamoDbTable<I> table = getTable();
        AttributeValue attributeValue = AttributeValue.builder().s(pk).build();
        Key.Builder key = Key.builder().partitionValue(attributeValue);
        sk.ifPresent(v -> key.sortValue((String) v));
        return table.getItem(key.build());
    }

    protected <T> List<I> findBySecundaryKeyAndAttribute(final T pk, final String indexName, String attributeName, String attributeValue,
            String operator) {
        List<I> resultList = new ArrayList<>();
        DynamoDbIndex<I> secIndex = this.getIndex(indexName);

        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue((String) pk).build());

        Iterable<Page<I>> results = secIndex.query(QueryEnhancedRequest.builder().queryConditional(queryConditional)
                .filterExpression(getExpression(attributeName, attributeValue, operator)).consistentRead(false).build());

        results.forEach(y -> resultList.addAll(y.items()));

        return resultList;
    }

    public <T> List<I> queryFilterItems(final T pk) {
        List<I> resultList = new ArrayList<>();
        DynamoDbTable<I> table = getTable();

        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue((String) pk).build());

        Iterable<Page<I>> results = table
                .query(QueryEnhancedRequest.builder().queryConditional(queryConditional).consistentRead(getConsistentRead()).build());

        results.forEach(y -> resultList.addAll(y.items()));

        return resultList;
    }

    public <T> List<I> queryFilterItemsByAttribute(final T pk, String attributeName, String attributeValue, String operator) {
        List<I> resultList = new ArrayList<>();
        DynamoDbTable<I> table = getTable();

        QueryConditional queryConditional = QueryConditional.keyEqualTo(Key.builder().partitionValue((String) pk).build());

        Iterable<Page<I>> results = table.query(QueryEnhancedRequest.builder().queryConditional(queryConditional)
                .filterExpression(getExpression(attributeName, attributeValue, operator)).consistentRead(getConsistentRead()).build());

        results.forEach(y -> resultList.addAll(y.items()));

        return resultList;
    }

    public Expression getExpression(String attributeName, String attributeValue, String operator) {
        Map<String, String> expressionAttributeNames = new HashMap<>();
        expressionAttributeNames.put("#".concat(attributeName), attributeName);

        Map<String, AttributeValue> expressionAttributeValues = new HashMap<>();
        expressionAttributeValues.put(":".concat(attributeName), AttributeValue.builder().s(attributeValue).build());

        String dynamoExpression = "#" + attributeName + " " + operator + " :" + attributeName;

        return Expression.builder().expressionNames(expressionAttributeNames).expressionValues(expressionAttributeValues)
                .expression(dynamoExpression).build();
    }

    public String getTableName() {
        return tableName;
    }

    public Boolean getConsistentRead() {
        return consistentRead;
    }

    public DynamoDbIndex<I> getIndex(String indexName) {
        return getTable().index(indexName);
    }
}
