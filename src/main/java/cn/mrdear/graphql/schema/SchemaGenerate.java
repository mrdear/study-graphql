package cn.mrdear.graphql.schema;

import graphql.schema.GraphQLSchema;

import java.io.IOException;

/**
 * @author Quding Ding
 * @since 2019-04-08
 */
public interface SchemaGenerate {

    /**
     * Schema生成器
     * @return schema
     */
    GraphQLSchema buildSchema() throws IOException;

}
