package com.xiaomi.micloud.document;

/**
 * Copyright (C) 2001-2018 by RapidMiner and the contributors
 *
 * Complete list of developers available at our web site:
 *
 * http://rapidminer.com
 *
 * This program is free software: you can redistribute it and/or modify it under the terms of the
 * GNU Affero General Public License as published by the Free Software Foundation, either version 3
 * of the License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT ANY WARRANTY; without
 * even the implied warranty of MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License along with this program.
 * If not, see http://www.gnu.org/licenses/.
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.lang.reflect.Constructor;
import java.nio.charset.Charset;
import java.util.zip.GZIPOutputStream;
import com.rapidminer.example.table.MemoryExampleTable;
import com.rapidminer.example.ExampleSet;
import com.rapidminer.operator.IOObject;
import com.rapidminer.operator.ResultObjectAdapter;
import com.rapidminer.tools.Ontology;
import com.rapidminer.tools.Tools;
import com.rapidminer.tools.XMLException;


/**
 * Implements wrapper methods of abstract example set. Implements all ResultObject methods.<br>
 *
 * Apart from the interface methods the implementing classes must have a public single argument
 * clone constructor. This constructor is invoked by reflection from the clone method. Do not forget
 * to call the superclass method.
 *
 * @author Ingo Mierswa, Simon Fischer
 */
public class SimpleDocument extends ResultObjectAdapter implements RMDocument {

    private static final long serialVersionUID = 8596141056047402798L;

    /** The table used for reading the examples from. */
    private String document;

    /**
     * Constructs a new SimpleExampleSet backed by the given example table. The example set
     * initially does not have any special attributes but all attributes from the given table will
     * be used as regular attributes.
     *
     * If you are constructing the example set from a {@link MemoryExampleTable}, you should use the
     * method {@link MemoryExampleTable#createExampleSet()} instead unless you are absolutely sure
     * what you are doing.
     */
    public SimpleDocument(String doc) {
        this.document = doc;
    }

    /** This method overrides the implementation of ResultObjectAdapter and returns "ExampleSet". */
    @Override
    public String getName() {
        return "RMDocument";
    }

    /** Counts the number of examples which fulfills the condition. */
    @Override
    public int size() {
        return document.length();
    }

    // --- Visualisation and toString() methods ---

    @Override
    public String toString() {
        StringBuffer str = new StringBuffer(this.getClass().getSimpleName() + ":" + Tools.getLineSeparator());
        str.append(size() + "words" + Tools.getLineSeparator());

        return str.toString();
    }

    // -------------------- File Writing --------------------
    @Override
    public void writeDataFile(File dataFile, int fractionDigits, boolean quoteNominal, boolean zipped, boolean append,
                              Charset encoding) throws IOException {
        try (OutputStream outStream = new FileOutputStream(dataFile, append);
             OutputStream zippedStream = zipped ? new GZIPOutputStream(outStream) : null;
             OutputStreamWriter osw = new OutputStreamWriter(zipped ? zippedStream : outStream, encoding);
             PrintWriter out = new PrintWriter(osw)) {
                out.println(document);
            }
    }


    public String getExtension() {
        return "txt";
    }

    public String getFileDescription() {
        return "attribute description file";
    }


    /** Returns the hash code of all attributes. */
    @Override
    public int hashCode() {
        return document.hashCode();
    }

    @Override
    public IOObject copy() {
        return clone();
    }

    /**
     * Clones the example set by invoking a single argument clone constructor. Please note that a
     * cloned example set has no information about the attribute statistics. That means, that
     * attribute statistics must be (re-)calculated after the clone was created.
     */
    @Override
    public RMDocument clone() {
        try {
            Class<? extends SimpleDocument> clazz = getClass();
            Constructor<? extends SimpleDocument> cloneConstructor = clazz.getConstructor(new Class[] { clazz });
            SimpleDocument result = cloneConstructor.newInstance(new Object[] { this });
            return result;
        } catch (IllegalAccessException e) {
            throw new RuntimeException("Cannot clone Document: " + e.getMessage());
        } catch (NoSuchMethodException e) {
            throw new RuntimeException("'" + getClass().getName() + "' does not implement clone constructor!");
        } catch (java.lang.reflect.InvocationTargetException e) {
            throw new RuntimeException("Cannot clone " + getClass().getName() + ": " + e + ". Target: "
                    + e.getTargetException() + ". Cause: " + e.getCause() + ".");
        } catch (InstantiationException e) {
            throw new RuntimeException("Cannot clone " + getClass().getName() + ": " + e);
        }
    }


    /**
     * Returns {@code true} if and only if the view implemented by this {@link ExampleSet} is thread-safe with respect
     * to read operations. This does not guarantee the thread-safety of the entire data set: both the underlying {@link
     * com.rapidminer.example.table.ExampleTable} and the set's attributes might be unsafe to be read from concurrently
     * and thus need to be checked separately.
     *
     * <p>A complete check is implemented by
     * {@link com.rapidminer.example.utils.ExampleSets#createThreadSafeCopy(ExampleSet)} which only creates a deep copy
     * if the thread-safety of the input example set is not guaranteed.
     *
     * @return {@code true} iff the view implemented by this example set is thread-safe w.r.t. to read operations
     * @see com.rapidminer.example.utils.ExampleSets#createThreadSafeCopy(ExampleSet)
     */
    public boolean isThreadSafeView() {
        return false;
    }
}
