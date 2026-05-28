import { FormEvent, useMemo, useState } from "react";

type AuthResponse = { accessToken: string };
type Me = { id: number; email: string; displayName: string; role: string; tokenMinutesBalance: number };
type Offer = { id: number; ownerId: number; ownerName: string; title: string; description: string; category: string; location: string; minutesPerHour: number; active: boolean };
type Need = { id: number; requesterId: number; requesterName: string; title: string; description: string; category: string; location: string; active: boolean };
type Booking = { id: number; offerId: number; offerTitle: string; requesterId: number; requesterName: string; providerId: number; providerName: string; startAt: string; endAt: string; tokenMinutes: number; status: string };
type Txn = { id: number; fromUserName: string; toUserName: string; minutes: number; memo: string; createdAt: string };

const categories = ["TECH", "DESIGN", "LANGUAGE", "TUTORING", "FITNESS", "HOME", "BUSINESS", "OTHER"];

async function api<T>(path: string, method = "GET", token?: string, body?: unknown): Promise<T> {
  const res = await fetch(path, {
    method,
    headers: {
      "Content-Type": "application/json",
      ...(token ? { Authorization: `Bearer ${token}` } : {})
    },
    body: body ? JSON.stringify(body) : undefined
  });
  if (!res.ok) {
    const text = await res.text();
    throw new Error(text || `Request failed: ${res.status}`);
  }
  return (await res.json()) as T;
}

export default function App() {
  const [token, setToken] = useState<string>("");
  const [me, setMe] = useState<Me | null>(null);
  const [offers, setOffers] = useState<Offer[]>([]);
  const [needs, setNeeds] = useState<Need[]>([]);
  const [bookings, setBookings] = useState<Booking[]>([]);
  const [txns, setTxns] = useState<Txn[]>([]);
  const [query, setQuery] = useState("");
  const [error, setError] = useState("");

  const [authMode, setAuthMode] = useState<"login" | "register">("register");
  const [email, setEmail] = useState("");
  const [displayName, setDisplayName] = useState("");
  const [password, setPassword] = useState("");

  const [skillTitle, setSkillTitle] = useState("");
  const [skillDesc, setSkillDesc] = useState("");
  const [category, setCategory] = useState("TECH");
  const [location, setLocation] = useState("Remote");
  const [minutesPerHour, setMinutesPerHour] = useState(60);

  const [bookOfferId, setBookOfferId] = useState<number | "">("");
  const [bookStart, setBookStart] = useState("");
  const [bookEnd, setBookEnd] = useState("");
  const [bookMinutes, setBookMinutes] = useState(60);

  const myUpcoming = useMemo(
    () => bookings.filter((b) => b.status !== "CANCELLED").slice(0, 6),
    [bookings]
  );

  async function refreshAll(activeToken = token) {
    const [meRes, offersRes, needsRes, bookingsRes, txnsRes] = await Promise.all([
      api<Me>("/api/me", "GET", activeToken),
      api<Offer[]>(`/api/offers?q=${encodeURIComponent(query)}`),
      api<Need[]>(`/api/needs?q=${encodeURIComponent(query)}`),
      api<Booking[]>("/api/bookings", "GET", activeToken),
      api<Txn[]>("/api/wallet/transactions", "GET", activeToken)
    ]);
    setMe(meRes);
    setOffers(offersRes);
    setNeeds(needsRes);
    setBookings(bookingsRes);
    setTxns(txnsRes);
  }

  async function handleAuth(e: FormEvent) {
    e.preventDefault();
    setError("");
    try {
      const body =
        authMode === "register"
          ? { email, displayName, password }
          : { email, password };
      const auth = await api<AuthResponse>(
        authMode === "register" ? "/api/auth/register" : "/api/auth/login",
        "POST",
        undefined,
        body
      );
      setToken(auth.accessToken);
      await refreshAll(auth.accessToken);
    } catch (err) {
      setError((err as Error).message);
    }
  }

  async function handleCreateOffer(e: FormEvent) {
    e.preventDefault();
    setError("");
    try {
      await api<Offer>("/api/offers", "POST", token, {
        title: skillTitle,
        description: skillDesc,
        category,
        location,
        minutesPerHour
      });
      setSkillTitle("");
      setSkillDesc("");
      await refreshAll();
    } catch (err) {
      setError((err as Error).message);
    }
  }

  async function handleCreateNeed(e: FormEvent) {
    e.preventDefault();
    setError("");
    try {
      await api<Need>("/api/needs", "POST", token, {
        title: skillTitle,
        description: skillDesc,
        category,
        location,
        minutesPerHour
      });
      setSkillTitle("");
      setSkillDesc("");
      await refreshAll();
    } catch (err) {
      setError((err as Error).message);
    }
  }

  async function handleCreateBooking(e: FormEvent) {
    e.preventDefault();
    if (bookOfferId === "") return;
    setError("");
    try {
      await api<Booking>("/api/bookings", "POST", token, {
        offerId: Number(bookOfferId),
        startAt: new Date(bookStart).toISOString(),
        endAt: new Date(bookEnd).toISOString(),
        tokenMinutes: bookMinutes
      });
      await refreshAll();
    } catch (err) {
      setError((err as Error).message);
    }
  }

  async function updateBooking(id: number, action: "confirm" | "complete" | "cancel") {
    setError("");
    try {
      await api<Booking>(`/api/bookings/${id}/${action}`, "POST", token);
      await refreshAll();
    } catch (err) {
      setError((err as Error).message);
    }
  }

  return (
    <div className="min-h-screen bg-slate-950 text-slate-100">
      <div className="mx-auto max-w-7xl p-4 md:p-8">
        <header className="mb-6 rounded-2xl border border-slate-800 bg-gradient-to-r from-slate-900 to-slate-900/60 p-6">
          <p className="text-sm font-semibold tracking-wide text-sky-300">SkillSwap Platform</p>
          <h1 className="mt-1 text-3xl font-bold md:text-4xl">Professional Time-Banking Network</h1>
          <p className="mt-2 max-w-3xl text-slate-300">
            Trade services with Time Tokens. Discover skills, publish offers and needs, and manage bookings in one responsive workspace.
          </p>
        </header>

        {error && <div className="mb-4 rounded-xl border border-rose-600/30 bg-rose-900/30 p-3 text-sm text-rose-200">{error}</div>}

        {!token || !me ? (
          <section className="mx-auto max-w-xl rounded-2xl border border-slate-800 bg-slate-900/60 p-6">
            <div className="mb-4 flex gap-2">
              <button className={`rounded-lg px-3 py-2 text-sm ${authMode === "register" ? "bg-sky-500 text-white" : "bg-slate-800"}`} onClick={() => setAuthMode("register")}>Register</button>
              <button className={`rounded-lg px-3 py-2 text-sm ${authMode === "login" ? "bg-sky-500 text-white" : "bg-slate-800"}`} onClick={() => setAuthMode("login")}>Login</button>
            </div>
            <form onSubmit={handleAuth} className="space-y-3">
              <input className="w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2" placeholder="Email" value={email} onChange={(e) => setEmail(e.target.value)} />
              {authMode === "register" && (
                <input className="w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2" placeholder="Display Name" value={displayName} onChange={(e) => setDisplayName(e.target.value)} />
              )}
              <input type="password" className="w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2" placeholder="Password" value={password} onChange={(e) => setPassword(e.target.value)} />
              <button className="w-full rounded-lg bg-sky-500 px-4 py-2 font-semibold text-white hover:bg-sky-400">
                {authMode === "register" ? "Create Account" : "Sign In"}
              </button>
            </form>
          </section>
        ) : (
          <main className="grid gap-4 xl:grid-cols-12">
            <section className="space-y-4 xl:col-span-4">
              <article className="rounded-2xl border border-slate-800 bg-slate-900/60 p-5">
                <h2 className="text-lg font-semibold">User Dashboard</h2>
                <p className="mt-1 text-sm text-slate-300">{me.displayName} ({me.email})</p>
                <p className="mt-3 text-3xl font-bold text-emerald-300">{me.tokenMinutesBalance} min</p>
                <p className="text-sm text-slate-400">Current Time Token wallet balance</p>
                <button className="mt-4 rounded-lg bg-slate-800 px-3 py-2 text-sm" onClick={() => refreshAll()}>
                  Refresh Data
                </button>
              </article>

              <article className="rounded-2xl border border-slate-800 bg-slate-900/60 p-5">
                <h3 className="text-base font-semibold">Publish Offer / Need</h3>
                <form className="mt-3 space-y-2">
                  <input className="w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2 text-sm" placeholder="Title" value={skillTitle} onChange={(e) => setSkillTitle(e.target.value)} />
                  <textarea className="h-20 w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2 text-sm" placeholder="Description" value={skillDesc} onChange={(e) => setSkillDesc(e.target.value)} />
                  <div className="grid grid-cols-2 gap-2">
                    <select className="rounded-lg border border-slate-700 bg-slate-950 px-3 py-2 text-sm" value={category} onChange={(e) => setCategory(e.target.value)}>
                      {categories.map((c) => <option key={c}>{c}</option>)}
                    </select>
                    <input className="rounded-lg border border-slate-700 bg-slate-950 px-3 py-2 text-sm" placeholder="Location" value={location} onChange={(e) => setLocation(e.target.value)} />
                  </div>
                  <input type="number" className="w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2 text-sm" value={minutesPerHour} onChange={(e) => setMinutesPerHour(Number(e.target.value))} />
                  <div className="grid grid-cols-2 gap-2">
                    <button onClick={handleCreateOffer} className="rounded-lg bg-emerald-600 px-3 py-2 text-sm font-semibold">Post Offer</button>
                    <button onClick={handleCreateNeed} className="rounded-lg bg-indigo-600 px-3 py-2 text-sm font-semibold">Post Need</button>
                  </div>
                </form>
              </article>
            </section>

            <section className="space-y-4 xl:col-span-5">
              <article className="rounded-2xl border border-slate-800 bg-slate-900/60 p-5">
                <div className="mb-3 flex flex-col justify-between gap-2 md:flex-row md:items-center">
                  <h2 className="text-lg font-semibold">Skill Discovery Board</h2>
                  <input className="w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2 text-sm md:w-64" placeholder="Search skills..." value={query} onChange={(e) => setQuery(e.target.value)} />
                </div>
                <button onClick={() => refreshAll()} className="mb-3 rounded-lg bg-sky-600 px-3 py-1.5 text-sm font-medium">Apply Filter</button>
                <div className="space-y-2">
                  {offers.map((o) => (
                    <div key={o.id} className="rounded-xl border border-slate-800 bg-slate-950/60 p-3">
                      <div className="flex items-center justify-between">
                        <h4 className="font-semibold">{o.title}</h4>
                        <span className="text-xs text-slate-400">{o.category} • {o.location}</span>
                      </div>
                      <p className="mt-1 text-sm text-slate-300">{o.description}</p>
                      <p className="mt-1 text-xs text-sky-300">Provider: {o.ownerName} • {o.minutesPerHour} min/hour</p>
                    </div>
                  ))}
                </div>
                <h3 className="mt-5 text-sm font-semibold text-slate-200">Community Needs</h3>
                <div className="mt-2 space-y-2">
                  {needs.map((n) => (
                    <div key={n.id} className="rounded-xl border border-slate-800 bg-slate-950/60 p-3">
                      <h4 className="font-semibold">{n.title}</h4>
                      <p className="text-sm text-slate-300">{n.description}</p>
                      <p className="mt-1 text-xs text-indigo-300">Requested by {n.requesterName} • {n.category}</p>
                    </div>
                  ))}
                </div>
              </article>
            </section>

            <section className="space-y-4 xl:col-span-3">
              <article className="rounded-2xl border border-slate-800 bg-slate-900/60 p-5">
                <h2 className="text-lg font-semibold">Booking Calendar</h2>
                <form onSubmit={handleCreateBooking} className="mt-3 space-y-2">
                  <select className="w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2 text-sm" value={bookOfferId} onChange={(e) => setBookOfferId(Number(e.target.value))}>
                    <option value="">Select offer to book</option>
                    {offers.filter((o) => o.ownerId !== me.id).map((o) => <option key={o.id} value={o.id}>{o.title} ({o.ownerName})</option>)}
                  </select>
                  <input type="datetime-local" className="w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2 text-sm" value={bookStart} onChange={(e) => setBookStart(e.target.value)} />
                  <input type="datetime-local" className="w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2 text-sm" value={bookEnd} onChange={(e) => setBookEnd(e.target.value)} />
                  <input type="number" className="w-full rounded-lg border border-slate-700 bg-slate-950 px-3 py-2 text-sm" value={bookMinutes} onChange={(e) => setBookMinutes(Number(e.target.value))} />
                  <button className="w-full rounded-lg bg-violet-600 px-3 py-2 text-sm font-semibold">Create Booking</button>
                </form>
                <div className="mt-4 space-y-2">
                  {myUpcoming.map((b) => (
                    <div key={b.id} className="rounded-lg border border-slate-800 bg-slate-950/70 p-3 text-sm">
                      <p className="font-semibold">{b.offerTitle}</p>
                      <p className="text-xs text-slate-300">{new Date(b.startAt).toLocaleString()}</p>
                      <p className="text-xs text-slate-400">{b.status} • {b.tokenMinutes} min</p>
                      <div className="mt-2 flex gap-1">
                        <button onClick={() => updateBooking(b.id, "confirm")} className="rounded bg-emerald-700 px-2 py-1 text-xs">Confirm</button>
                        <button onClick={() => updateBooking(b.id, "complete")} className="rounded bg-sky-700 px-2 py-1 text-xs">Complete</button>
                        <button onClick={() => updateBooking(b.id, "cancel")} className="rounded bg-rose-700 px-2 py-1 text-xs">Cancel</button>
                      </div>
                    </div>
                  ))}
                </div>
              </article>

              <article className="rounded-2xl border border-slate-800 bg-slate-900/60 p-5">
                <h3 className="text-base font-semibold">Wallet Ledger</h3>
                <div className="mt-2 space-y-2">
                  {txns.slice(0, 8).map((t) => (
                    <div key={t.id} className="rounded-lg border border-slate-800 bg-slate-950/70 p-2 text-xs">
                      <p>{t.fromUserName} → {t.toUserName}</p>
                      <p className="text-emerald-300">{t.minutes} min</p>
                      <p className="text-slate-400">{new Date(t.createdAt).toLocaleString()}</p>
                    </div>
                  ))}
                </div>
              </article>
            </section>
          </main>
        )}
      </div>
    </div>
  );
}

