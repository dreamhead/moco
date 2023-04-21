package com.github.dreamhead.moco;

import com.github.dreamhead.moco.internal.Client;

public interface Request extends Message {
    Client getClient();
}
