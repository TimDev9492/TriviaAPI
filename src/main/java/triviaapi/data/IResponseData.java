package triviaapi.data;

public interface IResponseData {
    int responseCode = -1;
    default int responseCode() {
        return this.responseCode;
    };
}
