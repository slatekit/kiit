
import kotlin.Unit;
import org.junit.Assert;
import org.junit.Test;
import kiit.results.*;

/*
import static kiit.results.Builder.DefaultImpls.success;
import static kiit.results.builders.Results.INSTANCE.success;

public class OpsTestsJava  {

        @Test
        public void can_get_or_else() {

            Result<String,Err> result1 = success("peter parker");
            Assert.assertEquals("peter parker", ResultKt.getOrElse(result1, () -> ""));
        }


        @Test
        public void can_get_or_null() {
            Result<String,Err>  result1 = errored("name unknown");
            Assert.assertEquals(null, result1.getOrNull());
        }


        @Test
        public void can_map_success() {
            Result<String,Err>  result1 = success("peter parker");
            Result<String,Err>  result2 = result1.map( name -> "peter parker : spider-man");
            Assert.assertEquals("peter parker : spider-man", ResultKt.getOrElse(result2, () -> ""));
        }


        @Test
        public void can_flatMap_success() {
            Result<String,Err>  result1 = success("peter parker");
            Result<String,Err>  result2 = ResultKt.flatMap(result1, name  -> success("peter parker : spider-man"));
            Assert.assertEquals("peter parker : spider-man", ResultKt.getOrElse(result2, () -> "" ));
        }


        @Test
        public void can_handle_success() {
            Result<String,Err>  result1 = success("peter parker");
            Result<String,Err>  result2 = result1.map( name -> "peter parker : spider-man" );
            result2.onSuccess( name -> {
                Assert.assertEquals("peter parker : spider-man", name);
                return Unit.INSTANCE;
            });
        }


        @Test
        public void can_map_errored() {
            Result<String,Err>  result1 = errored("name unknown");
            Result<String,Err>  result2 = result1.map( name -> "peter parker : spider-man" );
            Assert.assertEquals("??", ResultKt.getOrElse(result2, () -> "??"));
        }


        @Test
        public void can_flatMap_errored() {
            Result<String,Err>  result1 = errored("name unknown");
            Result<String,Err>  result2 = ResultKt.flatMap(result1, name -> success("peter parker : spider-man"));
            Assert.assertEquals("??", ResultKt.getOrElse(result2, () -> "??"));
        }


        @Test
        public void can_handle_errored() {
            Result<String,Err>  result1 = errored("name unknown");
            Result<String,Err>  result2 = result1.map( name -> "peter parker : spider-man");
            //result2.onFailure( err -> {
                Assert.assertEquals(Codes.ERRORED.getCode(), result1.getCode());
                Assert.assertEquals("name unknown", result1.getMsg());
                //return Unit.INSTANCE;
            //});
        }


        @Test
        public void can_fold_success() {
            Result<String,Err> result1 = success("peter parker");

            // Default to spider-man
            String result = result1.fold(
                    name -> "peter parker : spider-man",
                    err  -> "a marvel character"
            );
            Assert.assertEquals(result, "peter parker : spider-man");
        }
}

*/