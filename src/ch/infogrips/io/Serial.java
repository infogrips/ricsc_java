package ch.infogrips.io;

import java.io.*;

public interface Serial
{
   public abstract void writeTo(SerialOutputStream out);
   public abstract Object readFrom(SerialInputStream in);
} 
