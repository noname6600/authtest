import { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import type { Order, OrderForm, OrderStatus, ApiResponseList } from "../api/types";
import { getOrders, createOrder, updateOrder, deleteOrder } from "../api/auth";
import "../css/Order.css";

export default function OrderPage() {
  const navigate = useNavigate();
  const PAGE_SIZE = 10;

  const [orders, setOrders] = useState<Order[]>([]);
  const [page, setPage] = useState<number>(0);
  const [totalPages, setTotalPages] = useState<number>(1);
  const [loading, setLoading] = useState<boolean>(false);

  const [showForm, setShowForm] = useState<boolean>(false);
  const [editingId, setEditingId] = useState<number | null>(null);
  const [formData, setFormData] = useState<OrderForm>({
    product: "",
    deliveryDate: "",
    status: "N",
  });

  const mapStatus = (s: OrderStatus) => {
    switch (s) {
      case "N": return "NEW";
      case "A": return "ACTIVE";
      case "C": return "CANCEL";
      default: return s;
    }
  };

  const loadOrders = async () => {
    setLoading(true);
    try {
      const res: ApiResponseList<Order> = await getOrders(page, PAGE_SIZE);
      setOrders(res.data);
      setTotalPages(res.totalPages);
    } catch (e: any) {
      alert(e.message);
    } finally {
      setLoading(false);
    }
  };

  useEffect(() => {
    loadOrders();
  }, [page]);

  const openCreateForm = () => {
    setEditingId(null);
    setFormData({
      product: "",
      deliveryDate: "",
      status: "N",
    });
    setShowForm(true);
  };

  const openEditForm = (order: Order) => {
    setEditingId(order.id);

    const [dd, mm, yyyy] = order.deliveryDate.split("/");
    const formatted = `${yyyy}-${mm}-${dd}`;

    setFormData({
      product: order.product,
      deliveryDate: formatted,
      status: order.status,
    });

    setShowForm(true);
  };

  const submitForm = async () => {
    const [yyyy, mm, dd] = formData.deliveryDate.split("-");
    const formatted = `${dd}/${mm}/${yyyy}`;

    const body: OrderForm = {
      product: formData.product,
      deliveryDate: formatted,
      status: formData.status,
    };

    try {
      if (editingId === null) {
        await createOrder(body);
      } else {
        await updateOrder(editingId, body);
      }
      setShowForm(false);
      loadOrders();
    } catch (e: any) {
      alert(e.message);
    }
  };

  const handleDelete = async (id: number) => {
    if (!confirm("Delete this order?")) return;
    try {
      await deleteOrder(id);
      loadOrders();
    } catch (e: any) {
      alert(e.message);
    }
  };

  return (
    <div className="order-container">

      {/* HEADER */}
      <div className="order-header">
        <h2>Orders</h2>
        <button className="back-btn" onClick={() => navigate("/profile")}>
          Back
        </button>
      </div>

      <button className="primary-btn order-create-btn" onClick={openCreateForm}>
        + Create
      </button>

      {loading ? (
        <p>Loading...</p>
      ) : (
        <table className="order-table">
          <thead>
            <tr>
              <th>Product</th>
              <th>Delivery Date</th>
              <th>Status</th>
              <th style={{ width: "150px" }}>Actions</th>
            </tr>
          </thead>
          <tbody>
            {orders.map(o => (
              <tr key={o.id}>
                <td>{o.product}</td>
                <td>{o.deliveryDate}</td>
                <td>{mapStatus(o.status)}</td>
                <td>
                  <button className="secondary-btn" onClick={() => openEditForm(o)}>Edit</button>
                  <button className="danger-btn" onClick={() => handleDelete(o.id)}>Delete</button>
                </td>
              </tr>
            ))}
          </tbody>
        </table>
      )}

      <div className="pagination">
        <button onClick={() => setPage(p => Math.max(0, p - 1))} disabled={page === 0}>
          Prev
        </button>
        <span>{page + 1} / {totalPages}</span>
        <button onClick={() => setPage(p => Math.min(totalPages - 1, p + 1))} disabled={page + 1 === totalPages}>
          Next
        </button>
      </div>

      {showForm && (
        <div className="popup-form-backdrop">
          <div className="popup-form">
            <h3>{editingId ? "Edit Order" : "Create Order"}</h3>

            <label>Product</label>
            <input
              type="text"
              value={formData.product}
              onChange={e => setFormData({ ...formData, product: e.target.value })}
            />

            <label>Delivery Date</label>
            <input
              type="date"
              value={formData.deliveryDate}
              onChange={e => setFormData({ ...formData, deliveryDate: e.target.value })}
            />

            <label>Status</label>
            <select
              value={formData.status}
              onChange={e => setFormData({ ...formData, status: e.target.value as OrderStatus })}
            >
              <option value="N">NEW</option>
              <option value="C">CANCEL</option>
              <option value="A">ACTIVE</option>
            </select>

            <div className="form-buttons">
              <button className="primary-btn" onClick={submitForm}>Save</button>
              <button className="secondary-btn" onClick={() => setShowForm(false)}>Cancel</button>
            </div>
          </div>
        </div>
      )}

    </div>
  );
}
