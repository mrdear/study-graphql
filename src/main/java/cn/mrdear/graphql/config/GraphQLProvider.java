package cn.mrdear.graphql.config;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import cn.mrdear.graphql.schema.SchemaGenerate;
import graphql.GraphQL;

import java.io.IOException;
import java.util.List;

/**
 * @author Quding Ding
 * @since 2019-04-07
 */
@Component
public class GraphQLProvider {

    @Autowired
    private List<SchemaGenerate> schemaGenerates;

    @Bean
    public GraphQL graphQL() throws IOException {
        GraphQL.Builder graphQL = null;

        for (SchemaGenerate schemaGenerate : schemaGenerates) {
            if (null == graphQL) {
                graphQL = GraphQL.newGraphQL(schemaGenerate.buildSchema());
            } else {
                graphQL.schema(schemaGenerate.buildSchema());
            }
        }

        if (null == graphQL) {
            throw new NullPointerException("graphQL builder can't be null");
        }

        return graphQL.build();
    }

}
