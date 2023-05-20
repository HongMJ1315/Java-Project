public interface IOIn {
    public void start() ;

    public int read(byte[] buffer, int offset, int length) ;

    public void close() ;
}
