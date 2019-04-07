package cn.mrdear.graphql.config;

import static graphql.schema.idl.TypeRuntimeWiring.newTypeWiring;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import cn.mrdear.graphql.fetcher.GraphQLDataFetchers;
import graphql.GraphQL;
import graphql.schema.DataFetcher;
import graphql.schema.GraphQLSchema;
import graphql.schema.idl.RuntimeWiring;
import graphql.schema.idl.SchemaGenerator;
import graphql.schema.idl.SchemaParser;
import graphql.schema.idl.TypeDefinitionRegistry;

import java.io.IOException;
import java.net.URL;
import java.util.Map;

/**
 * @author Quding Ding
 * @since 2019-04-07
 */
@Component
public class GraphQLProvider {

    @Autowired
    private GraphQLDataFetchers graphQLDataFetchers;

    @Bean
    public GraphQL graphQL() throws IOException {
        GraphQL graphQL = null;
        URL url = Resources.getResource("schema.graphqls");
        String sdl = Resources.toString(url, Charsets.UTF_8);
        GraphQLSchema graphQLSchema = buildSchema(sdl);
        graphQL = GraphQL.newGraphQL(graphQLSchema).build();
        return graphQL;
    }


    private GraphQLSchema buildSchema(String sdl) {
        // schema解释器
        TypeDefinitionRegistry typeRegistry = new SchemaParser().parse(sdl);
        // source fetcher信息
        RuntimeWiring runtimeWiring = buildWiring();

        // 绑定两者
        SchemaGenerator schemaGenerator = new SchemaGenerator();
        return schemaGenerator.makeExecutableSchema(typeRegistry, runtimeWiring);
    }

    private RuntimeWiring buildWiring() {
        return RuntimeWiring.newRuntimeWiring()
            .type(newTypeWiring("Query") // 注册dataFetcher
                .dataFetcher("bookById", graphQLDataFetchers.getBookByIdDataFetcher()))
            .type(newTypeWiring("Book")
                .dataFetcher("author", graphQLDataFetchers.getAuthorDataFetcher())
                .dataFetcher("pageCount",graphQLDataFetchers.getPageCountDataFetcher()))
            .build();
    }

    public DataFetcher getPageCountDataFetcher() {
        return dataFetchingEnvironment -> {
            Map<String,String> book = dataFetchingEnvironment.getSource();
            return book.get("totalPages");
        };
    }

}
